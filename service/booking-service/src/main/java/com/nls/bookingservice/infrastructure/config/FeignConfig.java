package com.nls.bookingservice.infrastructure.config;


import com.nls.bookingservice.infrastructure.messaging.EnhancedAuthContextFeignInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public EnhancedAuthContextFeignInterceptor authContextFeignInterceptor(AuditContext auditContext) {
        return new EnhancedAuthContextFeignInterceptor(auditContext);
    }

}
