package com.codefathers.cfkserver.exceptions.model.discount;

public class AlreadyExistCodeException extends Exception {
    public AlreadyExistCodeException() {
    }

    public AlreadyExistCodeException(String message) {
        super(message);
    }

    public AlreadyExistCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
