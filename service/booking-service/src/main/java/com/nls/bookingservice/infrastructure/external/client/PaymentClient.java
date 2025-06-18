package com.nls.bookingservice.infrastructure.external.client;

import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.PaymentRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "payment-service-client")
public interface PaymentClient {
    @GetMapping("/payment/booking/{bookingId}")
    ApiResponse<PaymentRes> getPaymentByBookingId(@PathVariable UUID bookingId);

    @PostMapping("/payment/bookings/batch")
    ApiResponse<List<PaymentRes>> getPaymentsByBookingIds(@RequestBody List<UUID> bookingIds);
}