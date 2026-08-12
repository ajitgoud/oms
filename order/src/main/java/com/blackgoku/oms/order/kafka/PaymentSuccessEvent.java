package com.blackgoku.oms.order.kafka;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentSuccessEvent(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String transactionId
) {}