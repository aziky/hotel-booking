package com.nls.bookingservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateBookingReq(
        UUID propertyId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        @Schema(type = "string", pattern = "dd-MM-yyyy", example = "24-05-2025")
        LocalDate checkInDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        @Schema(type = "string", pattern = "dd-MM-yyyy", example = "24-05-2025")
        LocalDate checkOutDate,
        Integer guestsCount,
        BigDecimal totalNight,
        BigDecimal pricePerNight,
        BigDecimal vat,
        BigDecimal totalAmount,
        BigDecimal subtotalAmount,
        String specialRequests,
        String paymentMethod,
        LocalDateTime expiresAt
) {
}
