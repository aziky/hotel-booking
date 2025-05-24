package com.nls.paymentservice.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("web-url")
public record WebUrlProperties(
        String host,
        String paymentSuccess,
        String paymentFail
) {
}
