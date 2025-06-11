package com.nls.common.dto.response;

import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;

@With
@Builder
public record BookingDetailsRes (
        String bookingId,
        String customerName,
        String customerEmail,
        String roomType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer numberOfNights,
        Integer guestsCount,
        BigDecimal totalAmount
){
}
