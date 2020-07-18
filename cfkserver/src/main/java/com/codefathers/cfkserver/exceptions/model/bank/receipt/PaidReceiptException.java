package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class PaidReceiptException extends Exception {
    public PaidReceiptException() {
    }

    public PaidReceiptException(String message) {
        super(message);
    }
}
