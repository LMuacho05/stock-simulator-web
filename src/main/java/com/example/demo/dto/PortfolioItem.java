package com.example.demo.dto;

public class PortfolioItem {
    private String ticker;
    private int totalQuantity;

    public  PortfolioItem(String ticker, int totalQuantity) {
        this.ticker = ticker;
        this.totalQuantity = totalQuantity;
    }

    public PortfolioItem() {}

    public String getTicker() {return this.ticker;}
    public int getTotalQuantity() {return this.totalQuantity;}
    public void setTicker(String ticker) {this.ticker = ticker;}
    public void setTotalQuantity(int totalQuantity) {this.totalQuantity = totalQuantity;}
}
