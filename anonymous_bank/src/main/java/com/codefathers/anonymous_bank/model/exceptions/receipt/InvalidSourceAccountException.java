package com.codefathers.anonymous_bank.model.exceptions.receipt;

public class InvalidSourceAccountException extends Exception {
    public InvalidSourceAccountException() {
    }

    public InvalidSourceAccountException(String message) {
        super(message);
    }
}
