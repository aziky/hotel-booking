package com.nls.paymentservice.api.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RevenueData(
        BigDecimal totalRevenue,
        Long completedPayments,
        Long pendingPayments
) {
}
