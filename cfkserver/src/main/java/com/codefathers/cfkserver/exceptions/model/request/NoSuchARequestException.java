package com.codefathers.cfkserver.exceptions.model.request;

public class NoSuchARequestException extends Exception {
    public NoSuchARequestException() {
    }

    public NoSuchARequestException(String message) {
        super(message);
    }

    public NoSuchARequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
