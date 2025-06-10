package com.nls.bookingservice.domain.repository;

import com.nls.bookingservice.domain.entity.PropertyDayPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyDayPriceRepository extends JpaRepository<PropertyDayPrice, UUID> {

    List<PropertyDayPrice> findByPropertyId(UUID propertyId);

    void deleteByPropertyId(UUID propertyId);
    PropertyDayPrice findByPropertyIdAndDayOfWeek(UUID propertyId, Integer dayOfWeek);
}