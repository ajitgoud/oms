package com.blackgoku.oms.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequest(

        @NotBlank(message = "Product name is required")
        String name,

        String description,

        @NotNull(message = "Product price is required")
        @DecimalMin(
                value = "0.01",
                message = "Product price must be greater than 0"
        )
        BigDecimal price,

        @NotNull(message = "Product active status is required")
        Boolean active,

        @NotNull(message = "Product version is required")
        Long version
) {
}