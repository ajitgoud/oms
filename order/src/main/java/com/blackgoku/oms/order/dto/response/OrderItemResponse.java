package com.blackgoku.oms.order.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long id,
        Long productId,
        Long quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}