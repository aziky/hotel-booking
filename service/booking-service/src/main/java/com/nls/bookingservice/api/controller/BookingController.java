package com.nls.bookingservice.api.controller;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.bookingservice.application.IBookingService;
import com.nls.bookingservice.shared.utils.SecurityUtil;
import com.nls.common.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
