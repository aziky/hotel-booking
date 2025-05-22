package com.nls.userservice.api.dto.request;

import java.time.LocalDate;

public record UpdateUserReq (
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String identification,
        String gender
) {
}
