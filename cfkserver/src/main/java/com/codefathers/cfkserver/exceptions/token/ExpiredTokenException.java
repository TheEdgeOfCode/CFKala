package com.codefathers.cfkserver.exceptions.token;

public class ExpiredTokenException extends Exception {
    public ExpiredTokenException() {
    }

    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
