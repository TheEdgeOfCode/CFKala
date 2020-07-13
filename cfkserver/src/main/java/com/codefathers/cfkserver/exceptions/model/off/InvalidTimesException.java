package com.codefathers.cfkserver.exceptions.model.off;

public class InvalidTimesException extends Exception {
    public InvalidTimesException() {
    }

    public InvalidTimesException(String message) {
        super(message);
    }

    public InvalidTimesException(String message, Throwable cause) {
        super(message, cause);
    }
}
