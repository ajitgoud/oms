package com.blackgoku.oms.payment.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "payment_attempts",
        indexes = {
                @Index(
                        name = "idx_payment_attempt_payment_id",
                        columnList = "payment_id"
                )
        }
)
public class PaymentAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(
            name = "idempotency_key",
            nullable = false,
            unique = true,
            length = 100
    )
    private String idempotencyKey;

    @Column(
            name = "request_hash",
            nullable = false,
            length = 64
    )
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentAttemptStatus status;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;
}