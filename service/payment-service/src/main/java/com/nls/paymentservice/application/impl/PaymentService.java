package com.nls.paymentservice.application.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.request.NotificationMessage;
import com.nls.common.dto.response.*;
import com.nls.common.enumration.PaymentMethod;
import com.nls.common.enumration.PaymentStatus;
import com.nls.common.enumration.QueueName;
import com.nls.common.enumration.TypeEmail;
import com.nls.paymentservice.api.dto.response.PayOSRes;
import com.nls.paymentservice.application.IPaymentService;
import com.nls.paymentservice.application.IVnpayGateway;
import com.nls.paymentservice.domain.entity.Payment;
import com.nls.paymentservice.domain.entity.PaymentLog;
import com.nls.paymentservice.domain.repository.PaymentLogRepository;
import com.nls.paymentservice.domain.repository.PaymentRepository;
import com.nls.paymentservice.infrastructure.config.AuditContext;
import com.nls.paymentservice.infrastructure.external.client.BookingServiceClient;
import com.nls.paymentservice.infrastructure.external.client.UserServiceClient;
import com.nls.paymentservice.infrastructure.external.dto.response.VnpayValidationResult;
import com.nls.paymentservice.infrastructure.messaging.RabbitProducer;
import com.nls.paymentservice.infrastructure.properties.HostProperties;
import com.nls.paymentservice.infrastructure.properties.PayOSProperties;
import com.nls.paymentservice.infrastructure.properties.WebUrlProperties;
import com.nls.paymentservice.shared.mapper.PaymentMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    PaymentRepository paymentRepository;
    IVnpayGateway vnpayGateway;
    PayOS payOS;
    AuditContext auditContext;
    WebUrlProperties webUrlProperties;
    PayOSProperties payOSProperties;
    PaymentMapper paymentMapper;
    PaymentLogRepository paymentLogRepository;
    BookingServiceClient bookingServiceClient;
    ObjectMapper objectMapper;
    RabbitProducer rabbitProducer;
    HostProperties hostProperties;
    UserServiceClient userServiceClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<CreatePaymentRes> createPayment(CreatePaymentReq request) {
        try {
            log.info("Start create payment with request {}", request);
            auditContext.setTemporaryUser(request.email());
            long orderCode = generateSafeOrderCode();
            Payment payment = Payment.builder()
                    .bookingId(request.bookingId())
                    .amount(request.totalAmount())
                    .paymentMethod(request.paymentMethod())
                    .paymentStatus(PaymentStatus.PENDING.name())
                    .orderCode(orderCode)
                    .build();

            paymentRepository.save(payment);

            String urlPayment;

            switch (PaymentMethod.valueOf(request.paymentMethod())) {
                case VNPAY: {
                    urlPayment = vnpayGateway.createPaymentUrl(request, payment.getId());
                    break;
                }
                case PAYOS: {
                    PaymentData paymentData = PaymentData.builder()
                            .orderCode(orderCode)
                            .description("nls-hotel")
                            .amount(request.totalAmount().setScale(0, RoundingMode.FLOOR).intValue())
                            .returnUrl(hostProperties.server() + payOSProperties.returnUrl())
                            .cancelUrl(hostProperties.server() + payOSProperties.returnUrl())
                            .build();
                    log.info("Creating payment link with data: {}", objectMapper.writeValueAsString(paymentData));
                    CheckoutResponseData payOSResponse = payOS.createPaymentLink(paymentData);
                    urlPayment = payOSResponse.getCheckoutUrl();
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid payment method");
                }
            }

            if (urlPayment == null) throw new RuntimeException("Payment not created");

            log.info("payment url: {}", urlPayment);

            CreatePaymentRes createPaymentRes = CreatePaymentRes.builder()
                    .paymentUrl(urlPayment)
                    .build();
            return ApiResponse.created(createPaymentRes);
        } catch (Exception e) {
            log.error("Error at creat payment cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    @Transactional
    public String handleVnpResponse(Map<String, String> params) {
        log.info("Start handle vnp response with response: {}", params);

        VnpayValidationResult result = vnpayGateway.handleVnpayIPN(params);
        String paymentId = params.get("vnp_TxnRef");

        Optional<Payment> paymentOpt = paymentRepository.findById(UUID.fromString(paymentId));
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("payment not found with " + paymentId);
        }

        Payment payment = paymentOpt.get();
        payment.setPaymentStatus(result.success() ? PaymentStatus.PAID.name() : PaymentStatus.FAILED.name());

        paymentRepository.save(payment);

        return result.redirectUrl();
    }

    @Override
    @Transactional
    public String handlePayOSResponse(PayOSRes payOSRes) {
        log.info("Start handle payOS response with response: {}", payOSRes.toString());
        Payment payment = paymentRepository.findByOrderCode(payOSRes.orderCode());

        if (payment == null) throw new RuntimeException("payment not found with " + payOSRes.orderCode());
        if (!"00".equals(payOSRes.code())) throw new RuntimeException("Invalid params");

        PaymentLog paymentLog = paymentMapper.convertPayOSResToPaymentLog(payOSRes);
        paymentLog.setOrderCode(payment);
        paymentLogRepository.save(paymentLog);
        auditContext.setTemporaryUser(payment.getCreatedBy());

        if (payOSRes.cancel() || "CANCELLED".equalsIgnoreCase(payOSRes.status())) {
            payment.setPaymentStatus(PaymentStatus.FAILED.name());
            paymentRepository.save(payment);

            return webUrlProperties.host() + webUrlProperties.paymentFail();
        }

        if ("PAID".equalsIgnoreCase(payOSRes.status())) {
            payment.setPaymentStatus(PaymentStatus.PAID.name());
            paymentRepository.save(payment);

            ApiResponse<BookingDetailsRes> bookingDetailsRes = bookingServiceClient.updateBookingById(payment.getBookingId());
            log.info("Response from the booking service: {}", bookingDetailsRes.data());


            ApiResponse<UserRes> userRes = userServiceClient.getUserById(bookingDetailsRes.data().userId());
            log.info("Response from the user service: {}", userRes);
            BookingDetailsRes bookingDetails = bookingDetailsRes.data()
                    .withCustomerEmail(userRes.data().email())
                    .withCustomerName(userRes.data().name());

            Map<String, String> payload = new HashMap<>();
            payload.put("paymentMethod", payment.getPaymentMethod());

            Map<String, Object> detailsMap = objectMapper.convertValue(bookingDetails, new TypeReference<>() {
            });
            detailsMap.forEach((key, value) -> payload.put(key, String.valueOf(value)));

            log.info("Start send email to rabbitmq server");
            NotificationMessage notificationMessage = NotificationMessage.builder()
                    .to(payment.getCreatedBy())
                    .type(TypeEmail.EMAIL_PAYMENT_SUCCESS.name())
                    .payload(payload)
                    .build();

            rabbitProducer.sendEmail(
                    QueueName.EMAIL_PAYMENT_SUCCESS.getExchangeName(),
                    QueueName.EMAIL_PAYMENT_SUCCESS.getRoutingKey(),
                    notificationMessage
            );

            return webUrlProperties.host() + webUrlProperties.paymentSuccess();
        }
        return webUrlProperties.host() + webUrlProperties.paymentFail();
    }


    private long generateSafeOrderCode() {
        int base = (int) (System.currentTimeMillis() / 1000) % 1_000_000_000;
        int random = new Random().nextInt(900) + 100;
        return Long.parseLong(base + "" + random);
    }
    @Override
    public ApiResponse<PaymentRes> getPaymentByBookingId(UUID bookingId) {
        try {
            log.info("Getting payment for booking: {}", bookingId);

            Optional<Payment> paymentOpt = paymentRepository.findByBookingId(bookingId);

            if (paymentOpt.isEmpty()) {
                log.info("No payment found for booking: {}", bookingId);
                return ApiResponse.notFound("Payment not found", null);
            }

            PaymentRes paymentRes = convertToPaymentRes(paymentOpt.get());
            return ApiResponse.ok(paymentRes);

        } catch (Exception e) {
            log.error("Error getting payment by booking ID: {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    @Override
    public ApiResponse<List<PaymentRes>> getPaymentsByBookingIds(List<UUID> bookingIds) {
        try {
            log.info("Getting payments for {} bookings", bookingIds.size());

            List<Payment> payments = paymentRepository.findByBookingIdIn(bookingIds);

            List<PaymentRes> paymentResList = payments.stream()
                    .map(this::convertToPaymentRes)
                    .toList();

            log.info("Found {} payments for requested bookings", paymentResList.size());
            return ApiResponse.ok(paymentResList);

        } catch (Exception e) {
            log.error("Error getting payments by booking IDs: {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    private PaymentRes convertToPaymentRes(Payment payment) {
        return PaymentRes.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .orderCode(payment.getOrderCode())
                .build();
    }
}
