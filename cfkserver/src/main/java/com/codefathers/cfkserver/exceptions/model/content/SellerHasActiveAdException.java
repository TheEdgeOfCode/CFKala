package com.codefathers.cfkserver.exceptions.model.content;

public class SellerHasActiveAdException extends Exception {
    public SellerHasActiveAdException() {
    }

    public SellerHasActiveAdException(String message) {
        super(message);
    }

    public SellerHasActiveAdException(String message, Throwable cause) {
        super(message, cause);
    }
}
