package com.nls.bookingservice.application.impl;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.bookingservice.api.dto.response.UserBookingRes;
import com.nls.bookingservice.application.IBookingService;
import com.nls.bookingservice.domain.entity.Booking;
import com.nls.bookingservice.domain.repository.BookingRepository;
import com.nls.bookingservice.infrastructure.external.client.PaymentClient;
import com.nls.bookingservice.infrastructure.external.client.PaymentServerClient;
import com.nls.bookingservice.shared.mapper.BookingMapper;
import com.nls.bookingservice.shared.utils.SecurityUtil;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.dto.response.PaymentRes;
import com.nls.common.enumration.BookingStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService implements IBookingService {

    static long BOOKING_EXPIRE_DURATION_MINUTES = 30;
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    PaymentServerClient paymentServerClient;
    PaymentClient paymentClient;

    @Override
    @Transactional
    public ApiResponse<CreateBookingRes> createBooking(CreateBookingReq request) {
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
    }

    @Transactional
    @Override
    public ApiResponse<BookingDetailsRes> updateBookingById(UUID bookingId) {
        log.info("Start handle get booking details with ID {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setBookingStatus(BookingStatus.PAID.name());
        bookingRepository.save(booking);

        BookingDetailsRes bookingDetailsRes = bookingMapper.convertBookingToBookingDetailsRes(booking);

        return ApiResponse.ok(bookingDetailsRes);
    }



    @Override
    public ApiResponse<List<UserBookingRes>> getUserBookings() {
        try {
            UUID userId = SecurityUtil.getCurrentUserId();
            log.info("Getting all bookings for user: {}", userId);

            List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);

            if (bookings.isEmpty()) {
                log.info("No bookings found for user: {}", userId);
                return ApiResponse.ok(List.of(), "No bookings found");
            }

            // Get booking IDs for external service calls
            List<UUID> bookingIds = bookings.stream()
                    .map(Booking::getId)
                    .toList();

            // Fetch payments from payment service
            Map<UUID, PaymentRes> paymentMap = fetchPayments(bookingIds);

            // Convert to response DTOs
            List<UserBookingRes> userBookings = bookings.stream()
                    .map(booking -> convertToUserBookingRes(booking,
                            paymentMap.get(booking.getId())))
                    .toList();

            log.info("Found {} bookings for user: {}", userBookings.size(), userId);
            return ApiResponse.ok(userBookings);

        } catch (Exception e) {
            log.error("Error getting user bookings: {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }

    private Map<UUID, PaymentRes> fetchPayments(List<UUID> bookingIds) {
        try {
            log.info("Fetching payments for {} bookings", bookingIds.size());
            ApiResponse<List<PaymentRes>> paymentResponse = paymentClient.getPaymentsByBookingIds(bookingIds);

            if (paymentResponse.code() == 200 && paymentResponse.data() != null) {
                return paymentResponse.data().stream()
                        .collect(Collectors.toMap(PaymentRes::getBookingId, payment -> payment));
            }

            log.warn("Failed to fetch payments or empty response. Code: {}, Message: {}",
                    paymentResponse.code(), paymentResponse.message());
            return new HashMap<>();

        } catch (Exception e) {
            log.error("Error fetching payments from payment service: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private UserBookingRes convertToUserBookingRes(Booking booking, PaymentRes payment) {
        UserBookingRes.UserBookingResBuilder builder = UserBookingRes.builder()
                .id(booking.getId())
                .propertyId(booking.getPropertyId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guestsCount(booking.getGuestsCount())
                .totalNight(booking.getTotalNight())
                .pricePerNight(booking.getPricePerNight())
                .vat(booking.getVat())
                .subtotalAmount(booking.getSubtotalAmount())
                .totalAmount(booking.getTotalAmount())
                .expiresAt(booking.getExpiresAt())
                .bookingStatus(booking.getBookingStatus())
                .specialRequests(booking.getSpecialRequests());

        // Add payment details if available
        if (payment != null) {
            builder.paymentMethod(payment.getPaymentMethod())
                    .paymentStatus(payment.getPaymentStatus())
                    .paymentAmount(payment.getAmount());
        }

        return builder.build();
    }
}
