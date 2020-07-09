package com.codefathers.cfkserver.exceptions.model.category;

public class RepeatedFeatureException extends Exception {
    public RepeatedFeatureException() {
        super("This Feature Is Already In This Category");
    }

    public RepeatedFeatureException(String message) {
        super(message);
    }

    public RepeatedFeatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
