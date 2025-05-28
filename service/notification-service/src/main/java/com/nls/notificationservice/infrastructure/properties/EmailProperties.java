package com.nls.notificationservice.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mailjet")
public record EmailProperties (
        String apiKey,
        String secretKey,
        String email,
        String name
) {
}