package com.codefathers.cfkserver.exceptions.model.product;

public class ProductNotAvailableException extends Exception {
    public ProductNotAvailableException() {
    }

    public ProductNotAvailableException(String message) {
        super(message);
    }

}
