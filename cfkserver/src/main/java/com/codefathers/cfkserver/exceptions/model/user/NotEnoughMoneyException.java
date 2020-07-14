package com.codefathers.cfkserver.exceptions.model.user;

public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException(long difference) {
        super(String.valueOf(difference));
    }

    public NotEnoughMoneyException() {
    }
}
