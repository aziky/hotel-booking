package com.nls.bookingservice.application.impl;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.bookingservice.application.IBookingService;
import com.nls.bookingservice.domain.entity.Booking;
import com.nls.bookingservice.domain.repository.BookingRepository;
import com.nls.bookingservice.infrastructure.external.client.PaymentServerClient;
import com.nls.bookingservice.infrastructure.external.client.UserServiceClient;
import com.nls.bookingservice.shared.mapper.BookingMapper;
import com.nls.bookingservice.shared.utils.SecurityUtil;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.dto.response.UserRes;
import com.nls.common.enumration.BookingStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService implements IBookingService {

    static long BOOKING_EXPIRE_DURATION_MINUTES = 30;
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    PaymentServerClient paymentServerClient;
    private final UserServiceClient userServiceClient;

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
            ApiResponse<CreatePaymentRes> paymentResponse = paymentServerClient.createPayment(createPaymentReq);

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

    @Override
    public ApiResponse<BookingDetailsRes> getBookingDetails(UUID bookingId) {
        try {
            log.info("Start handle get booking details with ID {}", bookingId);
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            ApiResponse<UserRes> userRes = userServiceClient.getUserById(booking.getUserId());
            BookingDetailsRes bookingDetailsRes = bookingMapper.convertBookingToBookingDetailsRes(booking);
            bookingDetailsRes = bookingDetailsRes.withCustomerName(userRes.data().name())
                    .withCustomerEmail(userRes.data().email());

            return ApiResponse.ok(bookingDetailsRes);
        } catch (Exception e) {
            log.error("Error at get booking detail cause by: {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
}
