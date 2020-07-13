package com.codefathers.cfkserver.exceptions.model.discount;

public class NoMoreDiscount extends Exception {
    public NoMoreDiscount() {
    }

    public NoMoreDiscount(String message) {
        super(message);
    }

    public NoMoreDiscount(String message, Throwable cause) {
        super(message, cause);
    }
}
