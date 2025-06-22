package com.nls.paymentservice.infrastructure.external.client;

import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name = "booking-service")
public interface BookingServiceClient {

    String BASE = "/api/booking";

    @PutMapping(BASE + "/{bookingId}")
    ApiResponse<BookingDetailsRes> updateBookingById(@PathVariable("bookingId") UUID bookingId);

}
