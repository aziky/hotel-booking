package com.nls.paymentservice.application.impl;

import com.github.f4b6a3.tsid.TsidCreator;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.enumration.PaymentMethod;
import com.nls.common.enumration.PaymentStatus;
import com.nls.paymentservice.application.IPaymentService;
import com.nls.paymentservice.application.IVnpayGateway;
import com.nls.paymentservice.domain.entity.Payment;
import com.nls.paymentservice.domain.repository.PaymentRepository;
import com.nls.paymentservice.infrastructure.config.AuditContext;
import com.nls.paymentservice.infrastructure.external.dto.response.VnpayValidationResult;
import com.nls.paymentservice.infrastructure.properties.PayOSProperties;
import com.nls.paymentservice.infrastructure.properties.WebUrlProperties;
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

    @Transactional
    @Override
    public ApiResponse<CreatePaymentRes> createPayment(CreatePaymentReq request) {
        try {
            log.info("Start create payment with request {}", request);
            auditContext.setTemporaryUser(request.email());
            Payment payment = Payment.builder()
                    .bookingId(request.bookingId())
                    .amount(request.totalAmount())
                    .paymentMethod(request.paymentMethod())
                    .paymentStatus(PaymentStatus.PENDING.name())
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
                            .orderCode(TsidCreator.getTsid().toLong())
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
    public String handlePayOSResponse(Map<String, String> params) {
        return "";
    }

}
