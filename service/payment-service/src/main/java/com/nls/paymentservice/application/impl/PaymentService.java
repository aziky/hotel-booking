package com.nls.paymentservice.application.impl;

import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.enumration.PaymentStatus;
import com.nls.paymentservice.application.IPaymentService;
import com.nls.paymentservice.application.IVnpayGateway;
import com.nls.paymentservice.domain.entity.Payment;
import com.nls.paymentservice.domain.repository.PaymentRepository;
import com.nls.paymentservice.infrastructure.external.dto.response.VnpayValidationResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


    @Override
    public ApiResponse<CreatePaymentRes> createPayment(CreatePaymentReq request) {
        try {
            log.info("Start create payment with request {}", request);

            Payment payment = Payment.builder()
                    .bookingId(request.bookingId())
                    .amount(request.totalAmount())
                    .paymentMethod(request.paymentMethod())
                    .paymentStatus(PaymentStatus.PENDING.name())
                    .createdBy(request.email())
                    .updatedBy(request.email())
                    .build();

            paymentRepository.save(payment);

            String urlPayment = vnpayGateway.createPaymentUrl(request, payment.getId());
            if (urlPayment == null) throw new RuntimeException();

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
}
