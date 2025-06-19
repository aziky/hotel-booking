package com.nls.paymentservice.domain.repository;

import com.nls.paymentservice.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Your existing method
    Payment findByOrderCode(long orderCode);

    // Add these new methods to fix the compilation errors
    Optional<Payment> findByBookingId(UUID bookingId);

    @Query("SELECT p FROM Payment p WHERE p.bookingId IN :bookingIds")
    List<Payment> findByBookingIdIn(@Param("bookingIds") List<UUID> bookingIds);
}