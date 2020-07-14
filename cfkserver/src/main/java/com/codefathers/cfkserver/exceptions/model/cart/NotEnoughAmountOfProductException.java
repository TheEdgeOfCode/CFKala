package com.codefathers.cfkserver.exceptions.model.cart;

public class NotEnoughAmountOfProductException extends Exception {
    public NotEnoughAmountOfProductException(long amount) {
        super(String.valueOf(amount));
    }

    public NotEnoughAmountOfProductException() {
    }
}
