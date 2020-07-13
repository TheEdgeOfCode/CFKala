package com.codefathers.cfkserver.exceptions.model.product;

public class NoSuchSellerException extends Exception {
    public NoSuchSellerException() {
    }

    public NoSuchSellerException(String message) {
        super(message);
    }

    public NoSuchSellerException(String message, Throwable cause) {
        super(message, cause);
    }
}
