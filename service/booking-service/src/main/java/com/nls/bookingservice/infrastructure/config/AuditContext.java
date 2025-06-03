package com.nls.bookingservice.infrastructure.config;

import com.nls.common.shared.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AuditContext {

    private static final ThreadLocal<CustomUserDetails> temporaryUser = new ThreadLocal<>();

    public void setTemporaryUser(String userId, String email, String role) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        CustomUserDetails userDetails = new CustomUserDetails(
                UUID.fromString(userId), email, "", authorities
        );
        temporaryUser.set(userDetails);
    }

    public void setTemporaryUser(String email) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        CustomUserDetails userDetails = new CustomUserDetails(
                UUID.fromString(UUID.randomUUID().toString()), email, "", authorities
        );
        temporaryUser.set(userDetails);
    }

    public CustomUserDetails getTemporaryUser() {
        return temporaryUser.get();
    }

    public void clear() {
        temporaryUser.remove();
    }

    public boolean hasTemporaryUser() {
        return temporaryUser.get() != null;
    }

}
