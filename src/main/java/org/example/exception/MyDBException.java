package org.example.exception;

public class MyDBException extends Exception {
    public MyDBException(String message) {
        super(message);
    }

    public MyDBException(String message, Throwable cause) {
        super(message, cause);
    }
}
