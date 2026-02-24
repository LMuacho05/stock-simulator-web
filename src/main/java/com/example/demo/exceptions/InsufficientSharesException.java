package com.example.demo.exceptions;

public class InsufficientSharesException extends RuntimeException {
    static final long serialVersionUID = 0L;

    public InsufficientSharesException(String message) {
        super(message);
    }
}
