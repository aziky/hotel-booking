package com.nls.bookingservice.api.controller;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.bookingservice.api.dto.response.UserBookingRes;
import com.nls.bookingservice.application.IBookingService;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PutMapping("/{bookingId}")
    public ApiResponse<BookingDetailsRes> updateBookingById(@PathVariable("bookingId") UUID bookingId) {
        return bookingService.updateBookingById(bookingId);
    }
    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<UserBookingRes>>> getUserBookings() {
        return ResponseEntity.ok(bookingService.getUserBookings());
    }

    @GetMapping("/check")
    public ApiResponse<BookingDetailsRes> checkBooking(@RequestParam UUID userId, @RequestParam UUID propertyId, @RequestParam String bookingStatus) {
        return bookingService.checkBooking(userId, propertyId, bookingStatus);
    }
}
