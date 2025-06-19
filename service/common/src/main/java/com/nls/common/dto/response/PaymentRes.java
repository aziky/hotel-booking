package com.nls.common.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentRes {
    private UUID id;
    private UUID bookingId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private Long orderCode;
}