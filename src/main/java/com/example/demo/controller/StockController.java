package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.repository.StockDAO;
import com.example.demo.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            return "KEEP WORKING... ;)\n Good Bye :3";
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRegister user) {
        int newID = this.stockDAO.registerUser(user.getUsername());
        ApiResponse answer = new ApiResponse(true, "Welcome! New account registered with 10000$ in the wallet. ID: " + newID, newID);
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/balance/{userID}")
    public double getBalance(@PathVariable int userID) {
        return this.stockDAO.getBalance(userID);
    }

    @GetMapping("/portfolio/{userID}")
    public ResponseEntity<ApiResponse> getPortfolio(@PathVariable int userID) {
        List<PortfolioItem> portfolio = this.stockDAO.getPortfolio(userID);
        ApiResponse answer = new ApiResponse(true, "Portfolio loaded successfully.", portfolio);
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/history/{userID}")
    public ResponseEntity<ApiResponse> getHistory(@PathVariable int userID) {
        List<TransactionItem> history = this.stockDAO.getHistory(userID);
        ApiResponse answer = new ApiResponse(true, "History loaded successfully.", history);
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse> buyStock(@RequestBody TradeRequest tradeRequest) throws Exception {
            double cost = this.marketService.buyStock(tradeRequest.getUserId(), tradeRequest.getTicker(), tradeRequest.getQuantity());
            ApiResponse resposta = new ApiResponse(true, "Success!! You bought " + tradeRequest.getQuantity() + " shares of " +  tradeRequest.getTicker() + " for " + cost, null);
            return ResponseEntity.ok(resposta);
    }

    @GetMapping("/price/{ticker}")
    public ResponseEntity<ApiResponse> getPrice(@PathVariable String ticker) throws Exception {
        try {
            double currentPrice = this.marketService.getPrice(ticker);
            ApiResponse answer = new  ApiResponse(true, "Price loaded successfully.", currentPrice);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            ApiResponse error = new ApiResponse(false, "Could not find price for " + ticker, null);
            return ResponseEntity.ok(error);
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse> sellStock(@RequestBody TradeRequest request) throws  Exception {
            double profit = this.marketService.sellStock(request.getUserId(), request.getTicker(), request.getQuantity());
            ApiResponse resposta = new ApiResponse(true, "Success!! You sold " + request.getQuantity() + " shares of " + request.getTicker() + " for " + profit, null);
            return ResponseEntity.ok(resposta);
    }
}
