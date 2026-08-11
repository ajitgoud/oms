package com.blackgoku.oms.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequest(

        @NotNull(message = "Product id is required")
        Long productId,

        @NotNull(message = "Quantity is required")
        @Min(
                value = 1,
                message = "Quantity must be at least 1"
        )
        Long quantity
) {
}