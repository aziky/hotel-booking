package com.nls.bookingservice.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BookingDetailRes {
    private UUID id;
    private UUID propertyId;
    private String propertyName;
    private BigDecimal totalAmount;
    private String username;
    private String gmail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    private String paymentMethod;
}
