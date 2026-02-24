package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.TradeRequest;
import com.example.demo.repository.StockDAO;
import com.example.demo.service.MarketService;
import org.springframework.http.ResponseEntity;
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
            return "KEEP WORKING... ;)\n Good Bye :3";
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
    public ResponseEntity<ApiResponse> buyStock(@RequestBody TradeRequest tradeRequest) throws Exception {
            double cost = this.marketService.buyStock(tradeRequest.getUserId(), tradeRequest.getTicker(), tradeRequest.getQuantity());
            ApiResponse resposta = new ApiResponse(true, "Success!! You bought " + tradeRequest.getQuantity() + " shares of " +  tradeRequest.getTicker() + " for " + cost, null);
            return ResponseEntity.ok(resposta);
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse> sellStock(@RequestBody TradeRequest request) throws  Exception {
            double profit = this.marketService.sellStock(request.getUserId(), request.getTicker(), request.getQuantity());
            ApiResponse resposta = new ApiResponse(true, "Success!! You sold " + request.getQuantity() + " shares of " + request.getTicker() + " for " + profit, null);
            return ResponseEntity.ok(resposta);
    }
}
