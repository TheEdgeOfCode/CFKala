package com.codefathers.cfkserver.exceptions.model.log;

public class NoSuchALogException extends Exception {
    public NoSuchALogException(String id) {
        super(id);
    }

    public NoSuchALogException() {
    }
}
