package com.codefathers.cfkserver.exceptions.model.bank.account;

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
