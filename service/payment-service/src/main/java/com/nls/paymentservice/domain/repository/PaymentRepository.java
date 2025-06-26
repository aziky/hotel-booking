package com.nls.paymentservice.domain.repository;

import com.nls.paymentservice.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Existing methods
    Payment findByOrderCode(long orderCode);
    Optional<Payment> findByBookingId(UUID bookingId);

    @Query("SELECT p FROM Payment p WHERE p.bookingId IN :bookingIds")
    List<Payment> findByBookingIdIn(@Param("bookingIds") List<UUID> bookingIds);

    // NEW: Revenue calculation methods for admin dashboard

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.createdAt BETWEEN :fromDate AND :toDate")
    BigDecimal getTotalRevenueInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.createdAt BETWEEN :fromDate AND :toDate")
    BigDecimal getCompletedPaymentsInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                               @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'PENDING' AND p.createdAt BETWEEN :fromDate AND :toDate")
    BigDecimal getPendingPaymentsInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                             @Param("toDate") LocalDateTime toDate);
}