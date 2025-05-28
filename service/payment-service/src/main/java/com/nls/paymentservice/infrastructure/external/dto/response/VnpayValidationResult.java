package com.nls.paymentservice.infrastructure.external.dto.response;

public record VnpayValidationResult (
        boolean success,
        String redirectUrl
){
}
