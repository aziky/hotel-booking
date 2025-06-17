package com.nls.common.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@With
@Builder
public record BookingDetailsRes (
        UUID bookingId,
        UUID userId,
        String customerName,
        String customerEmail,
        String roomType,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate checkInDate,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate checkOutDate,
        Integer totalNight,
        Integer guestsCount,
        BigDecimal totalAmount
){
}
