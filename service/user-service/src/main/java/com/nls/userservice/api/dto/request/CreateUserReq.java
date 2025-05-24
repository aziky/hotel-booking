package com.nls.userservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record CreateUserReq(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,
        String identification,
        String gender,
        String profilePicture
) {
}
