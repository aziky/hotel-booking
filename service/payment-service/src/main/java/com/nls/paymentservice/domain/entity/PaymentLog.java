package com.nls.paymentservice.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "payment_logs", schema = "payment_service")
public class PaymentLog {
    @Id
    @Column(name = "order_code", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "payment_link_id")
    private String paymentLinkId;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "cancel")
    private Boolean cancel;

    @Size(max = 10)
    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "created_at")
    private Instant createdAt;

    @Size(max = 50)
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 50)
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code")
    @MapsId
    private Payment payment;

}