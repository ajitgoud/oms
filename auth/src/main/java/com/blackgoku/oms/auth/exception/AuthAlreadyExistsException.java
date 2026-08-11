package com.blackgoku.oms.auth.exception;

public class AuthAlreadyExistsException extends RuntimeException {

    public AuthAlreadyExistsException(String message) {
        super(message);
    }
}