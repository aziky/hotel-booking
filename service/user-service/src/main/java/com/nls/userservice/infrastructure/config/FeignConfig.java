package com.nls.userservice.infrastructure.config;

import com.nls.userservice.infrastructure.messaging.EnhancedAuthContextFeignInterceptor;
import com.nls.userservice.shared.utils.AuditContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public EnhancedAuthContextFeignInterceptor authContextFeignInterceptor(AuditContext auditContext) {
        return new EnhancedAuthContextFeignInterceptor(auditContext);
    }

}
