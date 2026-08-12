package com.blackgoku.oms.payment.event;

import lombok.Builder;

@Builder
public record PaymentFailedEvent(
        Long paymentId,
        Long orderId,
        String reason
) {}