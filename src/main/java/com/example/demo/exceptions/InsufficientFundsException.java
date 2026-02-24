package com.example.demo.exceptions;

public class InsufficientFundsException extends RuntimeException {
    static final long serialVersionUID = 0L;

    public InsufficientFundsException(String message) {
        super(message);
    }
}