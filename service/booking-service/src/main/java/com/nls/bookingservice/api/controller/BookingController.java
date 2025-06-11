package com.nls.bookingservice.api.controller;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.bookingservice.application.IBookingService;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("booking")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BookingController {

    IBookingService bookingService;

    @PostMapping
    public ApiResponse<CreateBookingRes> createBooking(@RequestBody CreateBookingReq request) {
        return bookingService.createBooking(request);
    }

    @GetMapping("/{bookingId}")
    public ApiResponse<BookingDetailsRes> getBookingDetails(@PathVariable("bookingId") UUID bookingId) {
        return bookingService.getBookingDetails(bookingId);
    }

}
