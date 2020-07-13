package com.codefathers.cfkserver.exceptions.model.discount;

public class NoSuchADiscountCodeException extends Exception {
    public NoSuchADiscountCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchADiscountCodeException() {
    }

    public NoSuchADiscountCodeException(String message) {
        super(message);
    }
}
