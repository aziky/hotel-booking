package com.nls.bookingservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CreatePropertyReq(
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
        String imageUrl
) {
        // Nested record for day prices
        public record DayPriceReq(
                @Schema(description = "Day of week (1=Monday, 2=Tuesday, 3=Wednesday, 4=Thursday, 5=Friday, 6=Saturday, 7=Sunday)", minimum = "1", maximum = "7")
                Integer dayOfWeek,
                @Schema(description = "Price for this day of week")
                BigDecimal price
        ) {}
}