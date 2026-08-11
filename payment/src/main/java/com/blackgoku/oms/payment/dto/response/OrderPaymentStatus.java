package com.blackgoku.oms.payment.dto.response;

public enum OrderPaymentStatus {
    PENDING,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    CONFIRMED,
    CANCELLED
}
