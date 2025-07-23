package com.nls.bookingservice.application.impl;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.api.dto.response.BookingDetailRes;
import com.nls.bookingservice.api.dto.response.CreateBookingRes;
import com.nls.bookingservice.api.dto.response.UserBookingRes;
import com.nls.bookingservice.application.IBookingService;
import com.nls.bookingservice.domain.entity.Booking;
import com.nls.bookingservice.domain.repository.BookingRepository;
import com.nls.bookingservice.infrastructure.external.client.PaymentClient;
import com.nls.bookingservice.infrastructure.external.client.UserClient;
import com.nls.bookingservice.shared.mapper.BookingMapper;
import com.nls.bookingservice.shared.utils.SecurityUtil;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.BookingDetailsRes;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.common.dto.response.PaymentRes;
import com.nls.common.dto.response.UserRes;
import com.nls.common.enumration.BookingStatus;
import jakarta.persistence.EntityNotFoundException;
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
    PaymentClient paymentClient;
    UserClient userClient;

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
        ApiResponse<CreatePaymentRes> paymentResponse = paymentClient.createPayment(createPaymentReq);

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
        log.info("update booking successfully");

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

    @Override
    public ApiResponse<BookingDetailsRes> checkBooking(UUID userId, UUID propertyId, String bookingStatus) {
        log.info("Start handle check booking for userId {} and propertyId {}", userId, propertyId);
        Booking booking = bookingRepository
                .findTopByUserIdAndPropertyIdAndBookingStatusOrderByCheckOutDateDesc(userId, propertyId, bookingStatus)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        BookingDetailsRes bookingDetailsRes = bookingMapper.convertBookingToBookingDetailsRes(booking);
        log.info("Converted booking to BookingDetailsRes: {}", bookingDetailsRes);
        return ApiResponse.ok(bookingDetailsRes, "Booking details fetched successfully");
    }
    @Override
    public ApiResponse<List<BookingDetailRes>> getAllUserBookings() {
        try {
            log.info("Getting all user bookings");

            List<Booking> bookings = bookingRepository.findAllByOrderByCreatedAtDesc();

            if (bookings.isEmpty()) {
                log.info("No bookings found");
                return ApiResponse.ok(List.of(), "No bookings found");
            }

            // Get booking IDs for external service calls
            List<UUID> bookingIds = bookings.stream()
                    .map(Booking::getId)
                    .toList();

            // Fetch payments from payment service
            Map<UUID, PaymentRes> paymentMap = fetchPayments(bookingIds);

            // Convert to response DTOs
            List<BookingDetailRes> bookingDetails = bookings.stream()
                    .map(booking -> convertToBookingDetailRes(booking, paymentMap.get(booking.getId())))
                    .toList();

            log.info("Found {} total bookings", bookingDetails.size());
            return ApiResponse.ok(bookingDetails);

        } catch (Exception e) {
            log.error("Error getting all user bookings: {}", e.getMessage());
            return ApiResponse.internalError();
        }
    }
    private Map<UUID, PaymentRes> fetchPayments(List<UUID> bookingIds) {
        try {
            log.info("Fetching payments for {} bookinbgs", bookingIds.size());
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
                .propertyName(booking.getProperty().getTitle())
                .roomType(booking.getProperty().getRoomType())
                .address(booking.getProperty().getAddress())
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
                .specialRequests(booking.getSpecialRequests())
                .createdAt(booking.getCreatedAt())
                ;

        // Add payment details if available
        if (payment != null) {
            builder.paymentMethod(payment.getPaymentMethod())
                    .paymentStatus(payment.getPaymentStatus())
                    .paymentAmount(payment.getAmount());
        }

        return builder.build();
    }

    private BookingDetailRes convertToBookingDetailRes(Booking booking, PaymentRes payment) {
        BookingDetailRes.BookingDetailResBuilder builder = BookingDetailRes.builder()
                .id(booking.getId())
                .propertyId(booking.getPropertyId())
                .propertyName(getPropertyName(booking))
                .totalAmount(booking.getTotalAmount())
                .createdAt(booking.getCreatedAt());

        if (payment != null) {
            builder.paymentMethod(payment.getPaymentMethod());
        }

        // Get user information
        try {
            ApiResponse<UserRes> userResponse = userClient.getUserById(booking.getUserId());
            if (userResponse.code() == 200 && userResponse.data() != null) {
                UserRes user = userResponse.data();
                builder.username(user.name())
                      .gmail(user.email());
            }
        } catch (Exception e) {
            log.error("Error fetching user information: {}", e.getMessage());
        }

        return builder.build();
    }

    private String getPropertyName(Booking booking) {
        if (booking.getProperty() != null) {
            return booking.getProperty().getTitle();
        }
        return null;
    }
}
