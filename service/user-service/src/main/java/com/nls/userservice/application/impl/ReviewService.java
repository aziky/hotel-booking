package com.nls.userservice.application.impl;

import com.nls.userservice.application.IReviewService;
import com.nls.userservice.domain.entity.Review;
import com.nls.userservice.domain.repository.ReviewRepository;
import com.nls.common.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewService implements IReviewService {

    ReviewRepository reviewRepository;

    @Override
    public ApiResponse<List<Review>> getReviewsByPropertyId(UUID propertyId) {
        try {
            log.info("Getting reviews for property: {}", propertyId);

            List<Review> reviews = reviewRepository.findByPropertyId(propertyId);

            log.info("Found {} reviews for property: {}", reviews.size(), propertyId);
            return ApiResponse.ok(reviews);

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
    public ApiResponse<Long> getReviewCount(LocalDateTime fromDate, LocalDateTime toDate) {
        try {
            log.info("Getting review count from {} to {}", fromDate, toDate);

            Long reviewCount = reviewRepository.countByCreatedAtBetween(fromDate, toDate);

            log.info("Found {} reviews in date range", reviewCount);
            return ApiResponse.ok(reviewCount);

        } catch (Exception e) {
            log.error("Error getting review count from {} to {}: {}", fromDate, toDate, e.getMessage(), e);
            return ApiResponse.internalError();
        }
    }
}