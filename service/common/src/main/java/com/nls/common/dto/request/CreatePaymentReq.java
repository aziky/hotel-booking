package com.nls.common.dto.request;

import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@With
public record CreatePaymentReq(
        UUID bookingId,
        UUID userId,
        String email,
        String paymentMethod,
        BigDecimal totalAmount
) {
}
