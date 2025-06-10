package com.nls.bookingservice.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nls.bookingservice.domain.entity.PropertyStatus;
import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@With
@Builder
public record PropertyDetailRes(
        UUID id,
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
        List<PropertyDayPriceRes> dayPrices,
        BigDecimal currentDayPrice, // Price for today
        BigDecimal serviceFee,
        Integer maxGuests,
        Integer bedrooms,
        Integer beds,
        BigDecimal bathrooms,
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime checkInTime,
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime checkOutTime,
        PropertyStatus status,
        List<PropertyImageRes> images,
        List<PropertyAmenityRes> amenities,
        List<PropertyCategoryRes> categories,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        String createdBy,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt,
        String updatedBy
) {
}