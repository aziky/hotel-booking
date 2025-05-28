package com.nls.bookingservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record CreatePropertyReq(
        UUID hostId,
        String title,
        String description,
        String propertyType,
        String roomType,
        String address,
        String provinceId,
        String districtId,
        String wardId,
        String zipCode,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal pricePerNight,
        BigDecimal serviceFee,
        Integer maxGuests,
        Integer bedrooms,
        Integer beds,
        BigDecimal bathrooms,
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "HH:mm", example = "14:00")
        LocalTime checkInTime,
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "HH:mm", example = "14:00")
        LocalTime checkOutTime,
        String status,
        String createdBy,
        String updatedBy
) {
}