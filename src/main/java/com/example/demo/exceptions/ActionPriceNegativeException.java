package com.example.demo.exceptions;

public class ActionPriceNegativeException extends RuntimeException {
    static final long serialVersionUID = 0L;

    public ActionPriceNegativeException(String message) {
        super(message);
    }
}
