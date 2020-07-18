package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class InvalidRecieptTypeException extends Exception {
    public InvalidRecieptTypeException() {
    }

    public InvalidRecieptTypeException(String message) {
        super(message);
    }

    public InvalidRecieptTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
