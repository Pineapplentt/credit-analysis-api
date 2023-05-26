package org.example.exception;

public class ApiConnectionException extends RuntimeException {
    public ApiConnectionException(String message) {
        super(message);
    }
}
