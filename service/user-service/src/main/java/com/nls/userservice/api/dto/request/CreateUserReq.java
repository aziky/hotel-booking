package com.nls.userservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record CreateUserReq(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber,
        @JsonFormat(pattern = "dd-MM-yyyy")
        @Schema(type = "string", pattern = "dd-MM-yyyy", example = "24-05-2025")
        LocalDate dateOfBirth,
        String identification,
        String gender,
        String profilePicture
) {
}
