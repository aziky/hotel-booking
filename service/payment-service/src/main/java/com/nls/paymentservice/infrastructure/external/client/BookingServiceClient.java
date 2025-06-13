package com.nls.paymentservice.infrastructure.external.client;

import com.nls.common.dto.response.BookingDetailsRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "booking-service")
public interface BookingServiceClient {

    String BASE = "/api/booking";

    @GetMapping(BASE + "/{bookingId}")
    BookingDetailsRes getBookingById(@PathVariable("bookingId") UUID bookingId);

}
