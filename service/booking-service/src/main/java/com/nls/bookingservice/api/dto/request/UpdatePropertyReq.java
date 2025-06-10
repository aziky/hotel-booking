package com.nls.bookingservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record UpdatePropertyReq(
        UUID id,
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
        List<DayPriceReq> dayPrices, // Changed from Map<String, String> pricePerNight
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
        String updatedBy
) {
        // Nested record for day prices
        public record DayPriceReq(
                @Schema(description = "Day of week (2=Monday, 3=Tuesday, 4=Wednesday, 5=Thursday, 6=Friday, 7=Saturday, 8=Sunday)", minimum = "2", maximum = "8")
                Integer dayOfWeek,
                @Schema(description = "Price for this day of week")
                BigDecimal price
        ) {}
}