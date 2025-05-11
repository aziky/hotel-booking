package com.nls.apigateway.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "swagger")
public record SwaggerServiceConfig(
        List<Service> services
) {
    public record Service(String name, String url) {}


}
