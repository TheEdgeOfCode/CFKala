package com.codefathers.cfkserver.exceptions.model.product;

public class NoSuchAProductException extends Throwable {
    public NoSuchAProductException() {
    }

    public NoSuchAProductException(String message) {
        super(message);
    }

    public NoSuchAProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
