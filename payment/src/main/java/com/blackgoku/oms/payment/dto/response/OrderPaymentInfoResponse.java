package com.blackgoku.oms.payment.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderPaymentInfoResponse(
        Long id,
        BigDecimal total,
        OrderPaymentStatus status
) { }
