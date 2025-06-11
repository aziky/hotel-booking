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
        String icon
) {
}