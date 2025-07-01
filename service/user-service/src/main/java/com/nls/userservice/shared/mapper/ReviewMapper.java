package com.nls.userservice.shared.mapper;

import com.nls.userservice.api.dto.request.CreateReviewReq;
import com.nls.userservice.api.dto.response.GetReviewRes;
import com.nls.userservice.domain.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review convertToReview(CreateReviewReq request);

    GetReviewRes convertToGetReviewRes(Review review);

}
