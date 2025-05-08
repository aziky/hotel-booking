package com.nls.bookingservice.domain.entity;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "properties", schema = "booking")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

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