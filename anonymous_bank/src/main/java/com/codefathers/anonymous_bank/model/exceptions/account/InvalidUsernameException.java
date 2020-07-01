package com.codefathers.anonymous_bank.model.exceptions.account;

public class InvalidUsernameException extends Exception {
    public InvalidUsernameException() {
    }

    public InvalidUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUsernameException(String message) {
        super(message);
    }
}
