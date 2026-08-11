package com.blackgoku.oms.customer.exception;

public class CustomerVersionConflictException extends RuntimeException {
    public CustomerVersionConflictException(String message) {
        super(message);
    }
}