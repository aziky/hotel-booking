
package com.nls.paymentservice.api.controller;

import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.dto.response.PaymentRes;
import com.nls.paymentservice.api.dto.response.PayOSRes;
import com.nls.paymentservice.api.dto.response.RevenueData; // Add this import
import com.nls.paymentservice.application.IPaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("payment")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    IPaymentService paymentService;

    @PostMapping
    public ApiResponse<CreatePaymentRes> createPayment(@RequestBody CreatePaymentReq request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/IPN/vnpay")
    public String handleVnpayIPN(@RequestParam Map<String, String> params) {
        return "redirect:" + paymentService.handleVnpResponse(params);
    }

    @GetMapping("/IPN/payos")
    public String handleVnpayOS(@ModelAttribute PayOSRes payOSRes) {
        return "redirect:" + paymentService.handlePayOSResponse(payOSRes);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentRes>> getPaymentByBookingId(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentByBookingId(bookingId));
    }

    @PostMapping("/bookings/batch")
    public ResponseEntity<ApiResponse<List<PaymentRes>>> getPaymentsByBookingIds(@RequestBody List<UUID> bookingIds) {
        return ResponseEntity.ok(paymentService.getPaymentsByBookingIds(bookingIds));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueData>> getRevenueData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Request to get revenue data from {} to {}", fromDate, toDate);

        // Convert LocalDate to LocalDateTime in service
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23, 59, 59);

        ApiResponse<RevenueData> response = paymentService.getRevenueData(fromDateTime, toDateTime);
        return ResponseEntity.ok(response);
    }
}
