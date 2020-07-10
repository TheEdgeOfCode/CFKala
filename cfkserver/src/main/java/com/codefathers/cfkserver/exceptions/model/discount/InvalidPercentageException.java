package com.codefathers.cfkserver.exceptions.model.discount;

public class InvalidPercentageException extends Exception {
    public InvalidPercentageException() {
    }

    public InvalidPercentageException(String message) {
        super(message);
    }

    public InvalidPercentageException(String message, Throwable cause) {
        super(message, cause);
    }
}
