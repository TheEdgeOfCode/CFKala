package com.codefathers.anonymous_bank.model.exceptions.receipt;

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
