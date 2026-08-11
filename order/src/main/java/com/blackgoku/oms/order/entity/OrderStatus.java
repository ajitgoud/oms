package com.blackgoku.oms.order.entity;

public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    CONFIRMED,
    CANCELLED
}