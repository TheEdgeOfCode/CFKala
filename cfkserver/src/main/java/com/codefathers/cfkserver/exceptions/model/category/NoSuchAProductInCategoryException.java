package com.codefathers.cfkserver.exceptions.model.category;

public class NoSuchAProductInCategoryException extends Exception {
    public NoSuchAProductInCategoryException() {
    }

    public NoSuchAProductInCategoryException(String message) {
        super(message);
    }

    public NoSuchAProductInCategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
