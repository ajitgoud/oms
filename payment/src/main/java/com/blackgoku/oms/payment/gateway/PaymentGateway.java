package com.blackgoku.oms.payment.gateway;

import com.blackgoku.oms.payment.entity.Payment;

public interface PaymentGateway {

    PaymentGatewayResult charge(Payment payment);

    PaymentGatewayResult getStatus(String transactionId);
}