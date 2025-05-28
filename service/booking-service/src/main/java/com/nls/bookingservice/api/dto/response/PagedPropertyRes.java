package com.nls.bookingservice.api.dto.response;

import lombok.Builder;
import lombok.With;

import java.util.List;

@With
@Builder
public record PagedPropertyRes(
        List<PropertyDetailRes> properties,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}