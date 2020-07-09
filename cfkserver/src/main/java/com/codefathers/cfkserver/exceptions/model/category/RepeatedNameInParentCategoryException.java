package com.codefathers.cfkserver.exceptions.model.category;

public class RepeatedNameInParentCategoryException extends Exception {
    public RepeatedNameInParentCategoryException() {
    }

    public RepeatedNameInParentCategoryException(String message) {
        super(message);
    }

    public RepeatedNameInParentCategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
