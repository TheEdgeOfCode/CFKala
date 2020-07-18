package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class InvalidDescriptionExcxeption extends Exception {
    public InvalidDescriptionExcxeption() {
    }

    public InvalidDescriptionExcxeption(String message) {
        super(message);
    }

    public InvalidDescriptionExcxeption(String message, Throwable cause) {
        super(message, cause);
    }
}
