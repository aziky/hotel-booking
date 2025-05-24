package com.nls.bookingservice.application.impl;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.bookingservice.application.IBookingService;
import com.nls.bookingservice.domain.entity.Booking;
import com.nls.bookingservice.domain.repository.BookingRepository;
import com.nls.bookingservice.infrastructure.external.client.PaymentClient;
import com.nls.bookingservice.shared.mapper.BookingMapper;
import com.nls.bookingservice.shared.utils.SecurityUtil;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.enumration.BookingStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService implements IBookingService {

    static long BOOKING_EXPIRE_DURATION_MINUTES = 30;
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    PaymentClient paymentClient;

    @Override
    @Transactional
    public ApiResponse<CreateBookingRes> createBooking(CreateBookingReq request) {
        try {
            log.info("Start handle create booking with request {}", request);
            Booking booking = bookingMapper.convertCreateBookingToBooking(request);
            booking.setUserId(SecurityUtil.getCurrentUserId());
            booking.setExpiresAt(LocalDateTime.now().plusMinutes(BOOKING_EXPIRE_DURATION_MINUTES));
            booking.setBookingStatus(BookingStatus.PENDING.name());
            bookingRepository.save(booking);

            log.info("Save booking successfully with ID: {}", booking.getId());

            CreatePaymentReq createPaymentReq = bookingMapper.convertCreateBookingToCreatePaymentReq(booking);
            createPaymentReq = createPaymentReq.withEmail(SecurityUtil.getCurrentEmail())
                    .withPaymentMethod(request.paymentMethod());
            ApiResponse<CreatePaymentRes> paymentResponse = paymentClient.createPayment(createPaymentReq);

            if (paymentResponse.code() != HttpStatus.CREATED.value()) {
                log.error("Create payment failed with response: {}", paymentResponse);
                throw new RuntimeException("Failed to create payment");
            }

            log.info("Payment created successfully for booking: {}", booking.getId());
            return ApiResponse.created(new CreateBookingRes(paymentResponse.data().paymentUrl()));
        } catch (Exception e) {
            log.error("Error at create booking cause by {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
}
