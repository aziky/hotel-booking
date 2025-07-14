package com.nls.bookingservice.domain.repository;

import com.nls.bookingservice.domain.entity.Property;
import com.nls.bookingservice.domain.entity.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    @Query("SELECT COUNT(p) FROM Property p WHERE p.createdAt BETWEEN :fromDate AND :toDate")
    Long countPropertiesInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                    @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COUNT(DISTINCT p.hostId) FROM Property p WHERE p.createdAt BETWEEN :fromDate AND :toDate")
    Long countDistinctHostsInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                       @Param("toDate") LocalDateTime toDate);
    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);
    Page<Property> findByHostId(UUID hostId, Pageable pageable);
}