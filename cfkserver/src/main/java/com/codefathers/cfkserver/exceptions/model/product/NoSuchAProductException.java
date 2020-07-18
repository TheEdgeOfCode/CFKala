package com.codefathers.cfkserver.exceptions.model.product;

public class NoSuchAProductException extends Exception {
    public NoSuchAProductException() {
    }

    public NoSuchAProductException(String message) {
        super(message);
    }

    public NoSuchAProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
