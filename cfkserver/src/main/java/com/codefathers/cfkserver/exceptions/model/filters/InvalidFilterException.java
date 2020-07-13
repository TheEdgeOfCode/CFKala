package com.codefathers.cfkserver.exceptions.model.filters;

public class InvalidFilterException extends Exception {
    public InvalidFilterException() {
    }

    public InvalidFilterException(String message) {
        super(message);
    }

    public InvalidFilterException(String message, Throwable cause) {
        super(message, cause);
    }
}
