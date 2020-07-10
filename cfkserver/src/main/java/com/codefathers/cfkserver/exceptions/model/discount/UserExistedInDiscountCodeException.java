package com.codefathers.cfkserver.exceptions.model.discount;

public class UserExistedInDiscountCodeException extends Exception {
    public UserExistedInDiscountCodeException() {
    }

    public UserExistedInDiscountCodeException(String message) {
        super(message);
    }

    public UserExistedInDiscountCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
