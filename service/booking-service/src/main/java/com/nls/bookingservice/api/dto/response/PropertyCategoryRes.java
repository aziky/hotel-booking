package com.nls.bookingservice.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@Builder
public record PropertyCategoryRes(
        UUID propertyId,
        UUID categoryId,
        String name,
        String description,
        String icon,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        String createdBy,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt,
        String updatedBy
) {
}