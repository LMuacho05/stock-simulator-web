package com.example.demo.controller;

import com.example.demo.dto.TradeRequest;
import com.example.demo.repository.StockDAO;
import com.example.demo.service.MarketService;
import org.springframework.web.bind.annotation.*;

@RestController
public class StockController {

    private final StockDAO stockDAO;
    private final MarketService marketService;

    public StockController(StockDAO stockDAO,  MarketService marketService) {
        this.stockDAO = stockDAO;
        this.marketService = marketService;
    }

    @GetMapping("/status")
    public String alive() {
        return "it's alive";
    }

    @GetMapping("/balance/{userID}")
    public double getBalance(@PathVariable int userID) {
        return this.stockDAO.getBalance(userID);
    }

    @GetMapping("/portfolio/{userID}")
    public String getPortfolio(@PathVariable int userID) {
        return this.stockDAO.getPortfolio(userID);
    }

    @PostMapping("/buy")
    public String buyStock(@RequestBody TradeRequest tradeRequest) {
        try {
            double cost = this.marketService.buyStock(tradeRequest.getUserId(), tradeRequest.getTicker(), tradeRequest.getQuantity());
            return "Success!! You bought " + tradeRequest.getQuantity() + " shares of " +  tradeRequest.getTicker() + " for " + cost;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/sell")
    public String sellStock(@RequestBody TradeRequest request) {
        try {
            double profit = this.marketService.sellStock(request.getUserId(), request.getTicker(), request.getQuantity());
            return "Success!! You sold " + request.getTicker() + " shares of " + request.getTicker() + " for " + profit;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
