package com.nls.bookingservice.infrastructure.external.client;

import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("payment-service")
public interface PaymentClient {

    String BASE = "/api/payment";

    @PostMapping(BASE)
    ApiResponse<CreatePaymentRes> createPayment(CreatePaymentReq request);

}
