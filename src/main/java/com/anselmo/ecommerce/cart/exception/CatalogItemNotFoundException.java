package com.anselmo.ecommerce.cart.exception;

public class CatalogItemNotFoundException extends RuntimeException {

    public CatalogItemNotFoundException(String message) {
        super(message);
    }
}
