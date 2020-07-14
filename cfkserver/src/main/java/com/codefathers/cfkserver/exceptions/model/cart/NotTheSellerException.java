package com.codefathers.cfkserver.exceptions.model.cart;

public class NotTheSellerException extends Exception {
    public NotTheSellerException() {
    }

    public NotTheSellerException(String message) {
        super(message);
    }
}
