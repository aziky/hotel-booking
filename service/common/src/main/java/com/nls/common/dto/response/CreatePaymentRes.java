package com.nls.common.dto.response;

import lombok.Builder;

@Builder
public record CreatePaymentRes(
        String paymentUrl
) {
}
