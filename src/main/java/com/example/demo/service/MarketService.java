package com.example.demo.service;

import com.example.demo.repository.StockDAO;
import com.example.demo.exceptions.*;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketService {

    private final StockDAO stockDAO;
    private final StockService stockService;

    @Autowired
    public MarketService(StockDAO stockDAO, StockService stockService) {
        this.stockDAO = stockDAO;
        this.stockService = stockService;
    }

    public double buyStock(int userID, String ticker, int quantity) throws IOException, InterruptedException {
        if(quantity <= 0){
            throw new QuantityNegativeException("The quantity to be purchased must be greater than zero.");
        }

        double actionPrice = stockService.getPrice(ticker);
        double totalCost = quantity * actionPrice;
        double currBalance = stockDAO.getBalance(userID);

        if(currBalance >= totalCost) {
            double newBalance =  currBalance - totalCost;
            stockDAO.updateBalance(userID, newBalance);
            stockDAO.registerPurchase(userID, ticker, quantity);
            stockDAO.recordTransaction(userID, ticker, quantity, "BUY", actionPrice);
            return totalCost;
        } else {
            // Usamos a TUA exceção
            throw new InsufficientFundsException("You don't have enough balance. Total cost: $" + totalCost);
        }
    }

    public double sellStock(int userID, String ticker, int quantity) throws IOException, InterruptedException {
        if(quantity <= 0) {
            throw new QuantityNegativeException("The quantity to be sold must be greater than zero.");
        }
        int quantityOwned = stockDAO.getQuantityOwned(userID, ticker);

        if(quantityOwned < quantity) {
            throw new InsufficientSharesException("You don't have enough shares of " + ticker + " to sell.");
        }

        double price = stockService.getPrice(ticker);
        double profit = price * quantity;
        double actualBalance = stockDAO.getBalance(userID);

        stockDAO.updateBalance(userID, actualBalance + profit);

        stockDAO.registerPurchase(userID, ticker, -quantity);
        stockDAO.recordTransaction(userID, ticker, quantity, "SELL", price);

        return profit;
    }
}