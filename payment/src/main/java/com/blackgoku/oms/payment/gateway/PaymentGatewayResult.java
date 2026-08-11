package com.blackgoku.oms.payment.gateway;

import lombok.Builder;

@Builder
public record PaymentGatewayResult(
        PaymentGatewayStatus status,
        String transactionId,
        String failureReason
) {

    public static PaymentGatewayResult success(
            String transactionId
    ) {
        return PaymentGatewayResult.builder()
                .status(PaymentGatewayStatus.SUCCESS)
                .transactionId(transactionId)
                .build();
    }

    public static PaymentGatewayResult failure(
            String reason
    ) {
        return PaymentGatewayResult.builder()
                .status(PaymentGatewayStatus.FAILED)
                .failureReason(reason)
                .build();
    }

    public static PaymentGatewayResult unknown(
            String reason
    ) {
        return PaymentGatewayResult.builder()
                .status(PaymentGatewayStatus.UNKNOWN)
                .failureReason(reason)
                .build();
    }
}