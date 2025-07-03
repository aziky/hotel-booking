package com.nls.bookingservice.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserBookingRes {
    private UUID id;
    private UUID propertyId;
    private String propertyName;
    private String roomType;
    private String address;
    private int maxGuests;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestsCount;
    private Integer totalNight;
    private BigDecimal pricePerNight;
    private BigDecimal vat;
    private BigDecimal subtotalAmount;
    private BigDecimal totalAmount;
    private LocalDateTime expiresAt;
    private String bookingStatus;
    private String specialRequests;
    private LocalDate createdAt;

    // Payment information from payment service
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal paymentAmount;


}