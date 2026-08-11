package com.blackgoku.oms.order.dto.response;

import com.blackgoku.oms.order.entity.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderResponse(
        Long id,
        Long customerId,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponse> items
) {
}