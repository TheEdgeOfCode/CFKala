package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class InvalidSourceAccountException extends Exception {
    public InvalidSourceAccountException() {
    }

    public InvalidSourceAccountException(String message) {
        super(message);
    }
}
