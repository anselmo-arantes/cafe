package com.anselmo.ecommerce.catalog.cart.exception;

public class CatalogItemNotFoundException extends RuntimeException {

    public CatalogItemNotFoundException(String message) {
        super(message);
    }
}
