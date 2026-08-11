package com.blackgoku.oms.inventory.exception;

public class InventoryAlreadyExistsException
        extends RuntimeException {

    public InventoryAlreadyExistsException(String message) {
        super(message);
    }
}