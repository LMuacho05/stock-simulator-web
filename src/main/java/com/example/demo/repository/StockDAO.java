package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
    public String getPortfolio(int userID) {
        String sql = "SELECT ticker, SUM(quantity) as total FROM portfolio WHERE user_id = ? GROUP BY ticker HAVING total > 0";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userID);

        if(rows.isEmpty()){
            return "\n(your portfolio is empty)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n----YOUR PORTFOLIO----\n");

        for (Map<String, Object> row : rows) {
            String ticker = (String) row.get("ticker");
            Number quantity = (Number) row.get("total");
            sb.append("* ").append(ticker).append(": ").append(quantity).append(" unites\n");
        }
        return sb.toString();
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
    public String getHistory(int userID) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY timestamp DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userID);

        if (rows.isEmpty()) {
            return "\n(No transactions found)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n--- TRANSACTION HISTORY ---\n");

        for (Map<String, Object> row : rows) {
            sb.append(String.format("[%s] %s %s: %s @ %s\n",
                    row.get("timestamp"), row.get("type"), row.get("ticker"), row.get("quantity"), row.get("price")));
        }
        return sb.toString();
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
}
