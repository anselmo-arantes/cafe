package com.anselmo.ecommerce.inventory.exception;

public class InventoryReservationNotFoundException extends RuntimeException {

    public InventoryReservationNotFoundException(String message) {
        super(message);
    }
}
