package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class EqualSourceDestException extends Exception {
    public EqualSourceDestException() {
    }

    public EqualSourceDestException(String message) {
        super(message);
    }

    public EqualSourceDestException(String message, Throwable cause) {
        super(message, cause);
    }
}
