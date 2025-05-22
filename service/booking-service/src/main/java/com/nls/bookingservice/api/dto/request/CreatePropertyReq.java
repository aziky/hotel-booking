package com.nls.bookingservice.api.dto.request;

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
        LocalTime checkInTime,
        LocalTime checkOutTime,
        String status,
        String createdBy,
        String updatedBy
) {
}