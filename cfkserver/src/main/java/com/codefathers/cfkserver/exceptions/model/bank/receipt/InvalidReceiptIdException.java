package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class InvalidReceiptIdException extends Exception {
    public InvalidReceiptIdException() {
    }

    public InvalidReceiptIdException(String message) {
        super(message);
    }
}
