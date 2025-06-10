package com.nls.paymentservice.infrastructure.config;


import com.nls.paymentservice.infrastructure.messaging.EnhancedAuthContextFeignInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public EnhancedAuthContextFeignInterceptor authContextFeignInterceptor(AuditContext auditContext) {
        return new EnhancedAuthContextFeignInterceptor(auditContext);
    }

}
