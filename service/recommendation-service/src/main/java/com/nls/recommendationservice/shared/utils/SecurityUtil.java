package com.nls.recommendationservice.shared.utils;

import com.nls.common.shared.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.UUID;

public class SecurityUtil {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Invalid token");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }

        if (principal instanceof User) {
            String username = ((User) principal).getUsername();
            return UUID.fromString(username); // yêu cầu username là UUID string
        }

        throw new RuntimeException("Can't get information from principal: " + principal.getClass().getName());
    }
}