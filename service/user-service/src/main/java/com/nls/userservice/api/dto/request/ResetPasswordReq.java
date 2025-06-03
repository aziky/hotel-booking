package com.nls.userservice.api.dto.request;

public record ResetPasswordReq(
        String token,
        String password
) {
}
