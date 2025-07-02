package com.nls.userservice.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetReviewRes  (
        UUID id,
        Integer rating,
        String comment,
        String createdBy,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime createdAt
){
}
