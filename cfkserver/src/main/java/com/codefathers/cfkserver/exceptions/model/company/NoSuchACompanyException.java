package com.codefathers.cfkserver.exceptions.model.company;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoSuchACompanyException extends Exception{
    public NoSuchACompanyException() {
    }

    public NoSuchACompanyException(String message) {
        super(message);
    }

    public NoSuchACompanyException(String message, Throwable cause) {
        super(message, cause);
    }
}
