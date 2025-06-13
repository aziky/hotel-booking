package com.nls.paymentservice.api.dto.response;

public record PayOSRes(
    String code,
    String id,
    boolean cancel,
    String status,
    Long orderCode
) {
}
