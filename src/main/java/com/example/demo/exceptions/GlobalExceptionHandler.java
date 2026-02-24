package com.example.demo.exceptions;

import com.example.demo.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // User Errors (HTTP 400 - Bad Request)
    @ExceptionHandler({
            InsufficientFundsException.class,
            InsufficientSharesException.class,
            QuantityNegativeException.class,
            QuantityOwnedException.class
    })
    public ResponseEntity<ApiResponse> handleUserBusinessRules(Exception e) {
        ApiResponse error = new ApiResponse(false, "Invalid Operation: " + e.getMessage(), null);
        return ResponseEntity.badRequest().body(error);
    }

    // System Errors (HTTP 500 - Internal Server Error)
    @ExceptionHandler(GlobalQuoteNullException.class)
    public ResponseEntity<ApiResponse> handleApiAlphaVantageError(GlobalQuoteNullException e) {
        ApiResponse error = new ApiResponse(false, "Market Data Error: " + e.getMessage(), null);
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(ActionPriceNegativeException.class)
    public ResponseEntity<ApiResponse> handleMarketDataError(ActionPriceNegativeException e) {
        ApiResponse error = new ApiResponse(false, "Critical Error: Invalid Share Price " + e.getMessage(), null);
        return ResponseEntity.internalServerError().body(error);
    }

    // Final Security Network
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllOtherExceptions(Exception e) {
        ApiResponse error = new ApiResponse(false, "Server Error Occur.", null);
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(error);
    }
}