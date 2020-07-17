package com.codefathers.anonymous_bank.model.exceptions.receipt;

public class InvalidAccountIdException extends Exception {
    public InvalidAccountIdException() {
    }

    public InvalidAccountIdException(String message) {
        super(message);
    }

    public InvalidAccountIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
