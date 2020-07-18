package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class NotEnoughMoneyAtSourceException extends Exception{
    public NotEnoughMoneyAtSourceException() {
    }

    public NotEnoughMoneyAtSourceException(String message) {
        super(message);
    }
}
