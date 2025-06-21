package com.nls.paymentservice.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payment_logs", schema = "payment_service")
public class PaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "order_code", nullable = false, referencedColumnName = "order_code")
    private Payment orderCode;

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

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @CreatedBy
    @Size(max = 50)
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @Size(max = 50)
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

}