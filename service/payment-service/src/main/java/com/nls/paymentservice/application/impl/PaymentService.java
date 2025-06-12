package com.nls.paymentservice.application.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.tsid.TsidCreator;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.request.NotificationMessage;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
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
import com.nls.common.dto.response.BookingDetailsRes;
import com.nls.paymentservice.infrastructure.external.dto.response.VnpayValidationResult;
import com.nls.paymentservice.infrastructure.messaging.RabbitProducer;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<CreatePaymentRes> createPayment(CreatePaymentReq request) {
        try {
            log.info("Start create payment with request {}", request);
            auditContext.setTemporaryUser(request.email());
            long orderCode = TsidCreator.getTsid().toLong();
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
                            .description("Thanh toan khach san")
                            .amount(request.totalAmount().setScale(0, RoundingMode.FLOOR).intValue())
                            .returnUrl(webUrlProperties.host() + payOSProperties.returnUrl())
                            .cancelUrl(webUrlProperties.host() + payOSProperties.returnUrl())
                            .build();
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
        try {
            log.info("Start handle payOS response with response: {}", payOSRes.toString());
            Payment payment = paymentRepository.findByOrderCode(payOSRes.orderCode());

            if (payment == null) throw new RuntimeException("payment not found with " + payOSRes.orderCode());
            if (!"00".equals(payOSRes.code())) throw new RuntimeException("Invalid params");

            PaymentLog paymentLog = paymentMapper.convertPayOSResToPaymentLog(payOSRes);
            paymentLogRepository.save(paymentLog);
            auditContext.setTemporaryUser(payment.getCreatedBy());

            if (payOSRes.cancel() || "CANCELLED".equalsIgnoreCase(payOSRes.status())) {
                payment.setPaymentStatus(PaymentStatus.FAILED.name());
                paymentRepository.save(payment);

                log.info("Start retrieve booking details with booking id {}", payment.getBookingId());
                BookingDetailsRes bookingDetailsRes = bookingServiceClient.getBookingById(payment.getBookingId());

                Map<String, String> payload = new HashMap<>();
                payload.put("bookingId", payment.getBookingId().toString());
                payload.put("paymentMethod", payment.getPaymentMethod());

                Map<String, Object> detailsMap = objectMapper.convertValue(bookingDetailsRes, new TypeReference<>() {});
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

                return webUrlProperties.host() + webUrlProperties.paymentFail();
            }

            if ("PAID".equalsIgnoreCase(payOSRes.status())) {
                payment.setPaymentStatus(PaymentStatus.PAID.name());
                paymentRepository.save(payment);
                return webUrlProperties.host() + webUrlProperties.paymentSuccess();
            }
        } catch (Exception e) {
            log.error("Error at handle payos response cause by: {}", e.getMessage());
        }
        return webUrlProperties.host() + webUrlProperties.paymentFail();
    }

}
