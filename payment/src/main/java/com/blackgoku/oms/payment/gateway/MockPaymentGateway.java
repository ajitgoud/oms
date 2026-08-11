package com.blackgoku.oms.payment.gateway;

import com.blackgoku.oms.payment.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResult charge(Payment payment) {

        return PaymentGatewayResult.success(
                UUID.randomUUID().toString()
        );
    }

    @Override
    public PaymentGatewayResult getStatus(
            String transactionId
    ) {
        return PaymentGatewayResult.unknown(
                "Mock gateway does not support status lookup"
        );
    }
}