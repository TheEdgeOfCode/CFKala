package com.codefathers.cfkserver.exceptions.model.off;

public class NoSuchAOffException extends Exception {
    public NoSuchAOffException() {
    }

    public NoSuchAOffException(String message) {
        super(message);
    }

    public NoSuchAOffException(String message, Throwable cause) {
        super(message, cause);
    }
}
