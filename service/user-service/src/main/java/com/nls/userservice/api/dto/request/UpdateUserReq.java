package com.nls.userservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record UpdateUserReq (
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,
        String profilePicture,
        String identification,
        String gender
) {
}
