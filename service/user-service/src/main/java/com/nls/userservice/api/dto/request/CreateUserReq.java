package com.nls.userservice.api.dto.request;

import java.time.LocalDate;

public record CreateUserReq(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String identification,
        String gender
) {
}
