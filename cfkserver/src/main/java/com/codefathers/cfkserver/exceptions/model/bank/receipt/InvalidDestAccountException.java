package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class InvalidDestAccountException extends Exception {
    public InvalidDestAccountException() {
    }

    public InvalidDestAccountException(String message) {
        super(message);
    }

    public InvalidDestAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
