package com.nls.recommendationservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "property_attractions", schema = "recommendation")
public class PropertyAttraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "attraction_id")
    private String attractionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attraction_id", insertable = false, updatable = false)
    private TouristAttraction attraction;
}