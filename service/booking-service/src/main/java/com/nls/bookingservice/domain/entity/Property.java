package com.nls.bookingservice.domain.entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "properties", schema = "booking")
public class Property {
    @Id
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "host_id", nullable = false)
    private UUID hostId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "property_type", nullable = false, length = 50)
    private String propertyType;

    @Column(name = "room_type", nullable = false, length = 50)
    private String roomType;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "province_id", nullable = false, length = 100)
    private String provinceId;

    @Column(name = "district_id", nullable = false, length = 100)
    private String districtId;

    @Column(name = "ward_id", nullable = false, length = 100)
    private String wardId;

    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "service_fee", precision = 10, scale = 2)
    @ColumnDefault("0")
    private BigDecimal serviceFee;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "bedrooms", nullable = false)
    private Integer bedrooms;

    @Column(name = "beds", nullable = false)
    private Integer beds;

    @Column(name = "bathrooms", nullable = false, precision = 3, scale = 1)
    private BigDecimal bathrooms;

    @Column(name = "check_in_time")
    @ColumnDefault("'15:00:00'")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    @ColumnDefault("'11:00:00'")
    private LocalTime checkOutTime;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    @ColumnDefault("NOW()")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private List<PropertyImage> images;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private List<PropertyAmenity> amenities;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private List<PropertyCategory> categories;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private List<PropertyDayPrice> dayPrices;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private List<Booking> bookings;

}
