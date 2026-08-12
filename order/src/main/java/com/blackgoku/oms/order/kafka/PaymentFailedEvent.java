package com.blackgoku.oms.order.kafka;

import lombok.Builder;

@Builder
public record PaymentFailedEvent(
        Long paymentId,
        Long orderId,
        String reason
) {}