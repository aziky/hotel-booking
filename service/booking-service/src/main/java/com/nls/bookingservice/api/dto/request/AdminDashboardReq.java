package com.nls.bookingservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
public record AdminDashboardReq(
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fromDate,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate toDate
) {
}