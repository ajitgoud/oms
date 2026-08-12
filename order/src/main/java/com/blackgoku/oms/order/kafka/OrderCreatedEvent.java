package com.blackgoku.oms.order.kafka;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        BigDecimal total
) {
}
