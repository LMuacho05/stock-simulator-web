package com.example.demo.service;
// Tools to JSON

import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.example.demo.exceptions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

/**
 * Service responsible for communicating with external API (Alpha Vantage).
 * It handles HTTP requests, JSON parsing via Gson, and API error validation.
 */
@Service
public class StockService {
    //Constants
    private static String API_KEY;
    private static final String URL_BASE = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE";

    // Static block to use myself API_KEY
    static {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            API_KEY = props.getProperty("api.key");
        } catch (IOException e) {
            throw new RuntimeException("CRÍTICO: Ficheiro config.properties não encontrado! Cria o ficheiro na raiz do projeto.");
        }
    }

    //Gson creation
    private final Gson gson = new Gson();

    /**
     * Receives a ticker and gets the real time price
     *
     * @param ticker the stock symbol of the request
     * @return the current price of the stock.
     * @throws IOException if there is any problem with the internet connection or HTTP.
     * @throws InterruptedException if the operation HTTP client is interrupted.
     * @throws GlobalQuoteNullException if the API returns a valid json but without price field or empty data
     */
    public double getPrice(String ticker) throws IOException, InterruptedException {
        // url for GET request
        String finalUrl = URL_BASE + "&symbol=" + ticker + "&apikey=" + API_KEY;

        try {
            // 1st Step: request to Internet via HttpClient to finalUrl to get the response text
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(finalUrl)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 2nd Step: Convert text Gson to objects
            JsonObject json = gson.fromJson(response.body(), JsonObject.class);

            if(!json.has("Global Quote")){
                throw new GlobalQuoteNullException();
            }
            JsonObject globalQuote =  json.getAsJsonObject("Global Quote");

            // If API fails or ticker didn't exist
            if (globalQuote == null || !globalQuote.has("05. price")) {
                throw new GlobalQuoteNullException();
            }

            String priceText = globalQuote.get("05. price").getAsString();

            // 3rd Step: Return the value
            return  Double.parseDouble(priceText);

        } catch (Exception e) {
            throw e;
        }
    }
}
