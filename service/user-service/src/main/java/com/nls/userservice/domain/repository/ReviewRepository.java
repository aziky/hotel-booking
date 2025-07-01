package com.nls.userservice.domain.repository;

import com.nls.userservice.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    List<Review> findByPropertyId(UUID propertyId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.propertyId = :propertyId")
    Double getAverageRatingByPropertyId(@Param("propertyId") UUID propertyId);
    Long countByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
}