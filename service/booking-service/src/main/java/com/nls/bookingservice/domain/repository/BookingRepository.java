package com.nls.bookingservice.domain.repository;

import com.nls.bookingservice.domain.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    List<Booking> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    Page<Booking> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt BETWEEN :fromDate AND :toDate")
    Long countBookingsInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                  @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingStatus = :status AND b.createdAt BETWEEN :fromDate AND :toDate")
    Long countBookingsByStatusInDateRange(@Param("status") String status,
                                          @Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate);

    Optional<Booking> findTopByUserIdAndPropertyIdAndBookingStatusOrderByCheckOutDateDesc(
            UUID userId,
            UUID propertyId,
            String bookingStatus
    );
}