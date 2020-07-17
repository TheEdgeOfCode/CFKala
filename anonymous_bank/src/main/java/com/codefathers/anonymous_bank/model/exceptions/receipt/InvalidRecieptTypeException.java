package com.codefathers.anonymous_bank.model.exceptions.receipt;

public class InvalidRecieptTypeException extends Exception {
    public InvalidRecieptTypeException() {
    }

    public InvalidRecieptTypeException(String message) {
        super(message);
    }

    public InvalidRecieptTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
