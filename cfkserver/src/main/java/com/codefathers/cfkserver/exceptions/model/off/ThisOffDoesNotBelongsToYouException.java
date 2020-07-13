package com.codefathers.cfkserver.exceptions.model.off;

public class ThisOffDoesNotBelongsToYouException extends Throwable {
    public ThisOffDoesNotBelongsToYouException() {
    }

    public ThisOffDoesNotBelongsToYouException(String message) {
        super(message);
    }

    public ThisOffDoesNotBelongsToYouException(String message, Throwable cause) {
        super(message, cause);
    }
}
