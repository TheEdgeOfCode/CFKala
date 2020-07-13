package com.codefathers.cfkserver.exceptions.model.discount;

public class NegativeMaxDiscountException extends Exception {
    public NegativeMaxDiscountException() {
    }

    public NegativeMaxDiscountException(String message) {
        super(message);
    }

    public NegativeMaxDiscountException(String message, Throwable cause) {
        super(message, cause);
    }
}
