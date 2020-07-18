package com.codefathers.cfkserver.exceptions.model.bank.receipt;

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
