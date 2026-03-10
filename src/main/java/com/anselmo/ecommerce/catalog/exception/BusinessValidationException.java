package com.anselmo.ecommerce.catalog.exception;

public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }
}
