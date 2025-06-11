package com.nls.common.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.With;

import java.time.LocalDate;

@With
@Builder
public record UserRes(
        String name,
        String email,
        String phoneNumber,
        String profilePicture,
        String identification,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,
        String gender,
        String token
) {

}
