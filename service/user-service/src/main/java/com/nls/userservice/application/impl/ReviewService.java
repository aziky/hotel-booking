package com.nls.userservice.application.impl;

import com.nls.common.dto.response.BookingDetailsRes;
import com.nls.common.enumration.BookingStatus;
import com.nls.userservice.api.dto.request.CreateReviewReq;
import com.nls.userservice.api.dto.response.GetReviewRes;
import com.nls.userservice.application.IReviewService;
import com.nls.userservice.domain.entity.Review;
import com.nls.userservice.domain.repository.ReviewRepository;
import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.infrastructure.external.client.BookingClient;
import com.nls.userservice.shared.mapper.ReviewMapper;
import com.nls.userservice.shared.utils.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewService implements IReviewService {

    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    BookingClient bookingClient;

    @Override
    public ApiResponse<List<GetReviewRes>> getReviewsByPropertyId(UUID propertyId) {
        try {
            log.info("Getting reviews for property: {}", propertyId);

            List<Review> reviews = reviewRepository.findByPropertyId(propertyId);
            List<GetReviewRes> response =  reviews.stream().map(reviewMapper::convertToGetReviewRes).toList();

            log.info("Found {} reviews for property: {}", response.size(), propertyId);
            return ApiResponse.ok(response);

        } catch (Exception e) {
            log.error("Error getting reviews for property {}: {}", propertyId, e.getMessage(), e);
            return ApiResponse.internalError();
        }
    }

    @Override
    public ApiResponse<Double> getAverageRatingByPropertyId(UUID propertyId) {
        try {
            log.info("Getting average rating for property: {}", propertyId);

            Double averageRating = reviewRepository.getAverageRatingByPropertyId(propertyId);

            // If no reviews exist, return 0.0 instead of null
            if (averageRating == null) {
                averageRating = 0.0;
                log.info("No reviews found for property: {}, returning 0.0", propertyId);
            } else {
                log.info("Average rating for property {}: {}", propertyId, averageRating);
            }

            return ApiResponse.ok(averageRating);

        } catch (Exception e) {
            log.error("Error getting average rating for property {}: {}", propertyId, e.getMessage(), e);
            return ApiResponse.internalError();
        }
    }

    @Override
    public ApiResponse<Long> getReviewCount(LocalDate fromDate, LocalDate toDate) {
        try {
            log.info("Getting review count from {} to {}", fromDate, toDate);

            Long reviewCount = reviewRepository.countByCreatedAtBetween(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX));

            log.info("Found {} reviews in date range", reviewCount);
            return ApiResponse.ok(reviewCount);

        } catch (Exception e) {
            log.error("Error getting review count from {} to {}: {}", fromDate, toDate, e.getMessage(), e);
            return ApiResponse.internalError();
        }
    }

    @Transactional
    @Override
    public ApiResponse<Void> addReview(CreateReviewReq request) {
        log.info("Star handling addReview for property: {}", request);
        Review review = reviewMapper.convertToReview(request);

        if (review.getRating() < 1 || review.getRating() > 5) {
            log.error("Invalid rating value: {}", review.getRating());
            return ApiResponse.badRequest("Rating must be between 1 and 5");
        }
        UUID userId = SecurityUtil.getCurrentUserId();

        ApiResponse<BookingDetailsRes> response = bookingClient.checkBooking(userId, request.propertyId(), BookingStatus.PAID.name());
        BookingDetailsRes bookingDetailsRes = response.data();
        log.info("Booking details retrieved: {}", bookingDetailsRes);

        review.setUserId(userId);
        review.setHostId(bookingDetailsRes.hostId());
        review.setBookingId(bookingDetailsRes.bookingId());

        reviewRepository.save(review);
        log.info("Create review successfully: {}", review);
        return ApiResponse.created();
    }
}