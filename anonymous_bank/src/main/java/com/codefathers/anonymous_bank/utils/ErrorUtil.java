package com.codefathers.anonymous_bank.utils;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

public class ErrorUtil {
    public static void sendError(HttpServletResponse response, String message, HttpStatus status){
        response.resetBuffer();
        response.setStatus(status.value());
        response.setHeader("Content-Type", "application/json");
        try {
            response.getOutputStream().print(String.format("{\"timestamp\": \"%s\"," +
                    "\"status\": %d,\"error\": \"%s\",\"message\": \"%s\"}",
                    new Timestamp(System.nanoTime()),status.value(),status.getReasonPhrase(),message));
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
