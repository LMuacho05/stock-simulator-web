package com.example.demo.dto;

import java.time.LocalDateTime;

public class TransactionItem {
    private String ticker;
    private int quantity;
    private String type;
    private double price;
    private LocalDateTime timestamp;

    public TransactionItem(String ticker, int quantity, String type, double price, LocalDateTime timestamp) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.type = type;
        this.price = price;
        this.timestamp = timestamp;
    }

    public TransactionItem() {}

    public String getTicker() {return this.ticker;}
    public void setTicker(String ticker) {this.ticker = ticker;}
    public int getQuantity() {return this.quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
    public String getType() {return this.type;}
    public void setType(String type) {this.type = type;}
    public double getPrice() {return this.price;}
    public void setPrice(double price) {this.price = price;}
    public LocalDateTime getTimestamp() {return this.timestamp;}
    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}
}
