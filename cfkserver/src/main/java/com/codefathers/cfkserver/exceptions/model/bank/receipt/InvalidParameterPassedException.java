package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class InvalidParameterPassedException extends Exception{
    public InvalidParameterPassedException() {
    }

    public InvalidParameterPassedException(String message) {
        super(message);
    }

    public InvalidParameterPassedException(String message, Throwable cause) {
        super(message, cause);
    }
}
