package com.anselmo.ecommerce.catalog.inventory.exception;

public class InventoryReservationNotFoundException extends RuntimeException {

    public InventoryReservationNotFoundException(String message) {
        super(message);
    }
}
