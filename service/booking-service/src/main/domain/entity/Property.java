package service.bookingservice.src.main.domain.entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "properties", schema = "booking")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

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
    private List<PropertyAttraction> attractions;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private List<Booking> bookings;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private List<Review> reviews;
}