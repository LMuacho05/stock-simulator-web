package com.example.demo.repository;

import com.example.demo.dto.PortfolioItem;
import com.example.demo.dto.TransactionItem;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object. It's the storage, it doesn't take decisions it only takes orders to read, save and
 * change data in tables.
 */
@Repository
public class StockDAO {

    private final JdbcTemplate jdbcTemplate;

    public StockDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /**
     * Give us the current balance from a specific user
     *
     * @param userID the unique ID of the user in the DB
     * @return The balance as double. Returns 0.0 if the user is not found. (could be modified to an exception)
     */
    public double getBalance(int userID) {
        String sql = "SELECT balance FROM users WHERE id = ?";
        Double balance = jdbcTemplate.queryForObject(sql, Double.class, userID);
        return balance != null ? balance : 0;
    }

    /**
     * Generate a formatted String report of the user's current stock holdings.
     * Aggregates all purchased shares found in the portfolio table for the user
     *
     * @param userID the unique ID of the user in the DB
     * @return a formatted String that contains all the stock and quantities of user's portfolio or a string mentioning
     * that is empty
     *
     */
    public List<PortfolioItem> getPortfolio(int userID) {
        String sql = "SELECT ticker, SUM(quantity) as total FROM portfolio WHERE user_id = ? GROUP BY ticker HAVING total > 0";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userID);

        List<PortfolioItem> portfolio = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            String ticker = (String) row.get("ticker");
            int quantity = ((Number) row.get("total")).intValue();
            portfolio.add(new PortfolioItem(ticker, quantity));
        }
        return portfolio;
    }

    /**
     *
     * @param userID
     * @param ticker
     * @return
     */
    public int getQuantityOwned(int userID, String ticker) {
        String sql = "SELECT SUM(quantity) FROM portfolio WHERE user_id = ? AND ticker = ?";

        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, userID, ticker);
        return total != null ? total : 0;
    }

    /**
     *
     * @param userID
     * @return
     */
    public List<TransactionItem> getHistory(int userID) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY timestamp DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userID);

        List<TransactionItem> history = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            String ticker = (String) row.get("ticker");
            int quantity = ((Number) row.get("quantity")).intValue();
            LocalDateTime dataHora = (LocalDateTime) row.get("timestamp");
            double price = ((Number) row.get("price")).doubleValue();
            String type = (String) row.get("type");
            TransactionItem t = new TransactionItem(ticker, quantity, type, price, dataHora);
            history.add(t);
        }
        return history;
    }

    /**
     * Update the balance of a specific user
     * usually used after a purchased (sale in the future) to reflect the money amount.
     *
     * @param userID the unique ID of the user in the DB
     * @param newBalance the final value to be saved in the DB
     */
    public void updateBalance(int userID, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        jdbcTemplate.update(sql, newBalance, userID);
    }

    /**
     * Save a new stock purchase in the portfolio table.
     * currently, this creates a new row for every transaction (doesn't aggregate quantities of the same shares in
     * different transaction)
     *
     * @param userID the unique ID of the user in the DB
     * @param ticker the symbol of the stock to purchase
     * @param quantity the number of shares to purchase
     */
    public void registerPurchase(int userID, String ticker, int quantity) {
        String sql = "INSERT INTO portfolio (user_id, ticker, quantity) VALUES (?,?,?)";
        jdbcTemplate.update(sql, userID, ticker, quantity);
    }

    /**
     *
     * @param userID
     * @param ticker
     * @param quantity
     * @param type
     * @param price
     */
    public void recordTransaction(int userID, String ticker, int quantity, String type, double price) {
        String sql = "INSERT INTO transactions (user_id, ticker, type, quantity, price, timestamp) VALUES (?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql, userID, ticker, type, quantity, price);
    }

    public int registerUser(String name) {
        String sql = "INSERT INTO users (username, balance) VALUES (?, 10000)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }
}
