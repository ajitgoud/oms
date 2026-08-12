package com.blackgoku.oms.payment.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentSuccessEvent(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String transactionId
) {}