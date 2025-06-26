package com.nls.bookingservice.application;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.bookingservice.api.dto.response.UserBookingRes;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;

import java.util.List;
import java.util.UUID;

public interface IBookingService {

    ApiResponse<CreateBookingRes> createBooking(CreateBookingReq request);

    ApiResponse<BookingDetailsRes> updateBookingById(UUID bookingId);

    ApiResponse<List<UserBookingRes>> getUserBookings();

    ApiResponse<BookingDetailsRes> checkBooking(UUID userId, UUID propertyId, String bookingStatus);

}
