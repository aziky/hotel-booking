package com.nls.userservice.infrastructure.external.client;

import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "booking-service")
public interface BookingClient {

    String BASE = "/api/booking";

    @GetMapping(BASE + "/check")
    ApiResponse<BookingDetailsRes> checkBooking(@RequestParam UUID userId, @RequestParam UUID propertyId, @RequestParam String bookingStatus);


}
