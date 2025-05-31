package com.nls.userservice.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fe-url")
public record FeProperties(
        String host,
        String confirmToken,
        String forgetPassword
) {
}
