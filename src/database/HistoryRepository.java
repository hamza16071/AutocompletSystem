package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryRepository {
    
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS search_history (" +
                     "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "  word TEXT NOT NULL," +
                     "  search_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
            System.out.println("[HistoryRepository] Table ready");
        } catch (SQLException e) { 
            System.err.println("[HistoryRepository] Create table error: " + e.getMessage());
        }
    }

    public void addToHistory(String word) {
        if (word == null || word.trim().isEmpty()) return;
        String sql = "INSERT INTO search_history (word) VALUES (?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, word.trim());
            ps.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("[HistoryRepository] Add history error: " + e.getMessage());
        }
    }

    public List<String> getRecentHistory(int limit) {
        List<String> history = new ArrayList<>();
        String sql =
"SELECT word FROM search_history ORDER BY search_time DESC LIMIT ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(rs.getString("word"));
            }
        } catch (SQLException e) { 
            System.err.println("[HistoryRepository] Get history error: " + e.getMessage());
        }
        return history;
    }
    
    public void clearHistory() {
        String sql = "DELETE FROM search_history";
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            System.err.println("[HistoryRepository] Clear history error: " + e.getMessage());
        }
    }
}