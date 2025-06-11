package com.nls.paymentservice.domain.repository;

import com.nls.paymentservice.domain.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
}
