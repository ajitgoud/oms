package com.blackgoku.oms.payment.dto.response;

import com.blackgoku.oms.payment.entity.PaymentMethod;
import com.blackgoku.oms.payment.entity.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status,
        PaymentMethod method,
        String gatewayTransactionId,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
}