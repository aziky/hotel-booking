package com.nls.bookingservice.domain.repository;

import com.nls.bookingservice.domain.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, UUID> {
}