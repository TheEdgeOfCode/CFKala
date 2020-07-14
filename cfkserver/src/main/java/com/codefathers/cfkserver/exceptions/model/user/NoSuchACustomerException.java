package com.codefathers.cfkserver.exceptions.model.user;

public class NoSuchACustomerException extends Exception {
    public NoSuchACustomerException() {
    }

    public NoSuchACustomerException(String message) {
        super(message);
    }
}
