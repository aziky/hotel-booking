package com.nls.paymentservice.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vnpay")
public record VnpayProperties(
        String responseHost,
        String vnpayUrl,
        String vnpayReturnUrl,
        String vnpTmpCode,
        String secretKey,
        String vnpayApiUrl,
        String vnpVersion,
        String vnpCommand
) {
}
