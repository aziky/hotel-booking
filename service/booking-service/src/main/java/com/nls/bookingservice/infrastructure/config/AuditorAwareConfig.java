package com.nls.bookingservice.infrastructure.config;

import com.nls.common.shared.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AuditorAwareConfig {

    private final AuditContext auditContext;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {

            if (auditContext.hasTemporaryUser()) {
                return Optional.of(auditContext.getTemporaryUser().getUsername());
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
                return Optional.of(auth.getName());
            }
            return Optional.of("system");
        };
    }


}
