package com.nls.bookingservice.application;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;

import java.util.UUID;

public interface IBookingService {

    ApiResponse<CreateBookingRes> createBooking(CreateBookingReq request);

    ApiResponse<BookingDetailsRes> getBookingDetails(UUID bookingId);

}
