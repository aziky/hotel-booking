package com.nls.userservice.api.dto.response;

import java.util.UUID;

public record GetReviewRes  (
        UUID id,
        Integer rating,
        String comment
){
}
