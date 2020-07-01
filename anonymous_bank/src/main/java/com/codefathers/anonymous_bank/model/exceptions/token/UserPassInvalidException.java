package com.codefathers.anonymous_bank.model.exceptions.token;

public class UserPassInvalidException extends Exception {
    public UserPassInvalidException() {
    }

    public UserPassInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
