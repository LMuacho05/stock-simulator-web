package com.example.demo.exceptions;

public class QuantityNegativeException extends RuntimeException {
    static final long serialVersionUID = 0L;

    public QuantityNegativeException(String message) {
        super(message);
    }
}
