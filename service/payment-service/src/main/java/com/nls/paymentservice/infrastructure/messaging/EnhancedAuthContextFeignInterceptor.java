package com.nls.paymentservice.infrastructure.messaging;

import com.nls.common.enumration.CustomHeader;
import com.nls.common.shared.CustomUserDetails;
import com.nls.paymentservice.infrastructure.config.AuditContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnhancedAuthContextFeignInterceptor implements RequestInterceptor {

    private final AuditContext auditContext;

    @Override
    public void apply(RequestTemplate template) {
        CustomUserDetails userDetails = null;

        if (auditContext.hasTemporaryUser()) {
            userDetails = auditContext.getTemporaryUser();
            log.debug("Using temporary user context for the Feign request");
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
                userDetails = (CustomUserDetails) auth.getPrincipal();
                log.debug("Using SecurityContext user for Feign request");
            }
        }

        if (userDetails != null) {
            template.header(CustomHeader.X_USER_ID, userDetails.getUserId().toString());
            template.header(CustomHeader.X_EMAIL, userDetails.getUsername());

            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            if (authorities != null && !authorities.isEmpty()) {
                String roles = authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));
                template.header(CustomHeader.X_ROLES, roles);
            }
            log.info("Added auth headers to Feign request - UserId: {}, Email: {}",
                    userDetails.getUserId(), userDetails.getUsername());
        } else {
            log.warn("No user context available for Feign request");
        }
    }
}
