package com.nls.userservice.api.controller;

import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.domain.entity.Review;
import com.nls.userservice.domain.repository.ReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("review")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewRepository reviewRepository;

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<Review>>> getReviewsByPropertyId(@PathVariable UUID propertyId) {
        List<Review> reviews = reviewRepository.findByPropertyId(propertyId);
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }

    @GetMapping("/property/{propertyId}/rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRatingByPropertyId(@PathVariable UUID propertyId) {
        Double averageRating = reviewRepository.getAverageRatingByPropertyId(propertyId);
        // If no reviews exist, return 0.0 instead of null
        if (averageRating == null) {
            averageRating = 0.0;
        }
        return ResponseEntity.ok(ApiResponse.ok(averageRating));
    }
}
