package com.codefathers.cfkserver.exceptions.model.user;

public class NoSuchAPackageException extends Exception {
    public NoSuchAPackageException() {}

    public NoSuchAPackageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchAPackageException(String message) {
        super(message);
    }
}
