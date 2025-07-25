package com.nls.bookingservice.infrastructure.external.client;

import com.nls.bookingservice.api.dto.response.RevenueData;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.dto.response.PaymentRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    String BASE = "/api/payment";

    @PostMapping(BASE)
    ApiResponse<CreatePaymentRes> createPayment(CreatePaymentReq request);


    @GetMapping(BASE + "/booking/{bookingId}")
    ApiResponse<PaymentRes> getPaymentByBookingId(@PathVariable UUID bookingId);


    @PostMapping(BASE + "/bookings/batch")
    ApiResponse<List<PaymentRes>> getPaymentsByBookingIds(@RequestBody List<UUID> bookingIds);


    @GetMapping("/api/payment/revenue")
    ApiResponse<RevenueData> getRevenueData(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    );
}