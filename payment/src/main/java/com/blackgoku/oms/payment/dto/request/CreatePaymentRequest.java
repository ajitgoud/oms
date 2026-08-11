package com.blackgoku.oms.payment.dto.request;

import com.blackgoku.oms.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreatePaymentRequest(

        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "Payment method is required")
        PaymentMethod method

) {
}