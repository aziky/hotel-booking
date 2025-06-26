package com.nls.userservice.application;

import com.nls.userservice.api.dto.request.CreateReviewReq;
import com.nls.userservice.domain.entity.Review;
import com.nls.common.dto.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IReviewService {

    ApiResponse<List<Review>> getReviewsByPropertyId(UUID propertyId);

    ApiResponse<Double> getAverageRatingByPropertyId(UUID propertyId);

    ApiResponse<Long> getReviewCount(LocalDateTime fromDate, LocalDateTime toDate);

    ApiResponse<Void> addReview(CreateReviewReq request);

}
