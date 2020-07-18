package com.codefathers.cfkserver.exceptions.model.bank.account;

public class PasswordsDoNotMatchException extends Exception {
    public PasswordsDoNotMatchException() {
    }

    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
