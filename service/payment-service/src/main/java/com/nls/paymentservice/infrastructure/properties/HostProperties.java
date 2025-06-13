package com.nls.paymentservice.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "host")
public record HostProperties(
        String server
) {
}
