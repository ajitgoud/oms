package com.blackgoku.oms.inventory.exception;

public class InventoryNotFoundException
        extends RuntimeException {

    public InventoryNotFoundException(String message) {
        super(message);
    }
}