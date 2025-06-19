package com.nls.bookingservice.api.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record RevenueData(
        BigDecimal totalRevenue,
        BigDecimal completedPayments,
        BigDecimal pendingPayments
) {
}