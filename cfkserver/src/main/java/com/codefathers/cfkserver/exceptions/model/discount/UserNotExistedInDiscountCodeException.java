package com.codefathers.cfkserver.exceptions.model.discount;

public class UserNotExistedInDiscountCodeException extends Exception {
    public UserNotExistedInDiscountCodeException() {
    }

    public UserNotExistedInDiscountCodeException(String message) {
        super(message);
    }

    public UserNotExistedInDiscountCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
