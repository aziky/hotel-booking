package com.nls.userservice.application;

import com.nls.userservice.api.dto.request.CreateReviewReq;
import com.nls.userservice.api.dto.response.GetReviewRes;
import com.nls.userservice.domain.entity.Review;
import com.nls.common.dto.response.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IReviewService {

    ApiResponse<List<GetReviewRes>> getReviewsByPropertyId(UUID propertyId);

    ApiResponse<Double> getAverageRatingByPropertyId(UUID propertyId);

    ApiResponse<Long> getReviewCount(LocalDate fromDate, LocalDate toDate);

    ApiResponse<Void> addReview(CreateReviewReq request);

}
