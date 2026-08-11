package com.blackgoku.oms.order.dto.response;

import com.blackgoku.oms.order.entity.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record OrderSummaryResponse(
        Long id,
        Long customerId,
        OrderStatus status,
        BigDecimal total,
        Instant createdAt
) {
}