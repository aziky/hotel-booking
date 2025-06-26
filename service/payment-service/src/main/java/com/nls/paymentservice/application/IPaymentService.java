package com.nls.paymentservice.application;

import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.dto.response.PaymentRes;
import com.nls.paymentservice.api.dto.response.PayOSRes;
import com.nls.paymentservice.api.dto.response.RevenueData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IPaymentService {

    ApiResponse<CreatePaymentRes> createPayment(CreatePaymentReq request);

    String handleVnpResponse(Map<String, String> params);

    String handlePayOSResponse(PayOSRes payOSRes);

    ApiResponse<PaymentRes> getPaymentByBookingId(UUID bookingId);

    ApiResponse<List<PaymentRes>> getPaymentsByBookingIds(List<UUID> bookingIds);
    ApiResponse<RevenueData> getRevenueData(LocalDateTime fromDate, LocalDateTime toDate);

}
