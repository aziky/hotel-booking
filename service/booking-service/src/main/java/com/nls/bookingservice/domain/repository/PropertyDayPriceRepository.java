package com.nls.bookingservice.domain.repository;

import com.nls.bookingservice.domain.entity.PropertyDayPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropertyDayPriceRepository extends JpaRepository<PropertyDayPrice, UUID> {

    List<PropertyDayPrice> findByPropertyId(UUID propertyId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PropertyDayPrice p WHERE p.propertyId = :propertyId")
    void deleteByPropertyId(UUID propertyId);
    List<PropertyDayPrice> findByPropertyIdOrderByDayOfWeek(UUID propertyId);

    Optional<PropertyDayPrice> findByPropertyIdAndDayOfWeek(UUID propertyId, Integer dayOfWeek);
}