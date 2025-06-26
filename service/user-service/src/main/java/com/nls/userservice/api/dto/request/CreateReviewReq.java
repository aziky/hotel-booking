package com.nls.userservice.api.dto.request;

import java.util.UUID;

public record CreateReviewReq (
        UUID propertyId,
        String comment,
        Integer rating
) {
}
