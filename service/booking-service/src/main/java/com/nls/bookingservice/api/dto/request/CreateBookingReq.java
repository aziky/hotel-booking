package com.nls.bookingservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBookingReq(
        UUID propertyId,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate checkInDate,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate checkOutDate,
        Integer guestsCount,
        BigDecimal totalNight,
        BigDecimal pricePerNight,
        BigDecimal vat,
        BigDecimal totalAmount,
        BigDecimal subtotalAmount,
        String specialRequests,
        String paymentMethod
) {
}
