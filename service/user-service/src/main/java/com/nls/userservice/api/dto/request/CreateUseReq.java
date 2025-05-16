package com.nls.userservice.api.dto.request;

import java.time.LocalDateTime;

public record CreateUseReq(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDateTime dateOfBirth,
        String identification
) {
}
