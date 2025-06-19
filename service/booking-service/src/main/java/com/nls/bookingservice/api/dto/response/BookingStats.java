package com.nls.bookingservice.api.dto.response;

import lombok.Builder;

@Builder
public record BookingStats(
        Long totalBookings,
        Long completedBookings,
        Long cancelledBookings
) {
}