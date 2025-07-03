package com.nls.userservice.api.controller;

import com.nls.common.dto.response.ApiResponse;
import com.nls.userservice.api.dto.request.CreateReviewReq;
import com.nls.userservice.api.dto.response.GetReviewRes;
import com.nls.userservice.application.IReviewService;
import com.nls.userservice.domain.entity.Review;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("review")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewController {

    IReviewService reviewService;

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<GetReviewRes>>> getReviewsByPropertyId(@PathVariable UUID propertyId) {
        log.info("Request to get reviews for property: {}", propertyId);
        return ResponseEntity.ok(reviewService.getReviewsByPropertyId(propertyId));
    }

    @GetMapping("/property/{propertyId}/rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRatingByPropertyId(@PathVariable UUID propertyId) {
        log.info("Request to get average rating for property: {}", propertyId);
        ApiResponse<Double> response = reviewService.getAverageRatingByPropertyId(propertyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/reviews/count")
    public ResponseEntity<ApiResponse<Long>> getReviewCount(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {

        log.info("Request to get review count from {} to {}", fromDate, toDate);
        ApiResponse<Long> response = reviewService.getReviewCount(fromDate, toDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ApiResponse<Void> createReview(@RequestBody CreateReviewReq request) {
        return reviewService.addReview(request);
    }

    @GetMapping()
    public ApiResponse<List<GetReviewRes>> getAllReviews() {
        return reviewService.getAllReview();
    }
}
