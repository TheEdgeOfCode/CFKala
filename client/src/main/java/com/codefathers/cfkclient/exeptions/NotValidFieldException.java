package com.codefathers.cfkclient.exeptions;

public class NotValidFieldException extends Exception {
    public NotValidFieldException(String field, String format) {
        super("Enter A Valid (" + format + ") int field (" + field + ").");
    }
}