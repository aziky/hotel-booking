package com.nls.userservice.shared.mapper;

import com.nls.userservice.api.dto.request.CreateReviewReq;
import com.nls.userservice.domain.entity.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review convertToReview(CreateReviewReq request);


}
