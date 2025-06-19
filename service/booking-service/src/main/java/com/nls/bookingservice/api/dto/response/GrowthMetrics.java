package com.nls.bookingservice.api.dto.response;

import lombok.Builder;

@Builder
public record GrowthMetrics(
        Double userGrowthRate,
        Double revenueGrowthRate
) {
}