package com.nls.bookingservice.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AdminDashboardRes(
        // Counts
        Long totalProperties,
        Long newUsers,
        Long totalHosts,

        // Revenue
        BigDecimal totalRevenue,
        BigDecimal completedPayments,
        BigDecimal pendingPayments,

        // Date range for context
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fromDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate toDate,

        // Additional metrics
        Long totalBookings,
        Long completedBookings,
        Long cancelledBookings,

        // Growth metrics (optional)
        Double userGrowthRate,
        Double revenueGrowthRate
) {
}