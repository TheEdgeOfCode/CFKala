package com.codefathers.cfkserver.exceptions.model.product;

public class EditorIsNotSellerException extends Exception {
    public EditorIsNotSellerException() {
    }

    public EditorIsNotSellerException(String message) {
        super(message);
    }

    public EditorIsNotSellerException(String message, Throwable cause) {
        super(message, cause);
    }
}
