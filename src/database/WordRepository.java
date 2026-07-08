package database;

import models.Word;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordRepository {

    // ── Schema ────────────────────────────────────────────────────────
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS words (" +
                     "  id        INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "  word      TEXT    NOT NULL UNIQUE COLLATE NOCASE," +
                     "  category  TEXT    NOT NULL DEFAULT 'mixed'," +
                     "  frequency INTEGER NOT NULL DEFAULT 1" +
                     ")";
        try (Connection c = DBConnection.getConnection();
             Statement  s = c.createStatement()) {
            s.execute(sql);
            s.execute("CREATE INDEX IF NOT EXISTS idx_word ON words(word COLLATE NOCASE)");
            s.execute("CREATE INDEX IF NOT EXISTS idx_freq ON words(frequency DESC)");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── Insert ────────────────────────────────────────────────────────
    public void insertWord(Word word) {
        String sql = "INSERT OR IGNORE INTO words (word, category, frequency) VALUES (?, ?, ?)";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, word.getWord().trim());
            ps.setString(2, word.getCategory());
            ps.setInt   (3, word.getFrequency());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void insertBatch(List<Word> words) {
        String sql = "INSERT OR IGNORE INTO words (word, category, frequency) VALUES (?, ?, ?)";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            c.setAutoCommit(false);
            for (Word w : words) {
                ps.setString(1, w.getWord().trim());
                ps.setString(2, w.getCategory());
                ps.setInt   (3, w.getFrequency());
                ps.addBatch();
            }
            ps.executeBatch();
            c.commit();
            c.setAutoCommit(true);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── Update frequency ─────────────────────────────────────────────
    public void incrementFrequency(String word) {
        String sql = "UPDATE words SET frequency = frequency + 5 WHERE LOWER(word) = LOWER(?)";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, word);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── Fetch all ────────────────────────────────────────────────────
    public List<Word> getAllWords() {
        List<Word> list = new ArrayList<>();
        String sql = "SELECT id, word, category, frequency FROM words ORDER BY frequency DESC";
        try (Connection c  = DBConnection.getConnection();
             Statement  s  = c.createStatement();
             ResultSet  rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Word(
                    rs.getInt   ("id"),
                    rs.getString("word"),
                    rs.getString("category"),
                    rs.getInt   ("frequency")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    

    // ── Utility ──────────────────────────────────────────────────────
    public int getWordCount() {
        try (Connection c  = DBConnection.getConnection();
             Statement  s  = c.createStatement();
             ResultSet  rs = s.executeQuery("SELECT COUNT(*) FROM words")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    // Add this method - for phrase suggestions
public List<Word> searchByPrefix(String prefix, int limit) {
    List<Word> list = new ArrayList<>();
    String sql = "SELECT id, word, category, frequency FROM words " +
                 "WHERE LOWER(word) LIKE LOWER(?) " +
                 "ORDER BY frequency DESC LIMIT ?";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, prefix.trim() + "%");
        ps.setInt(2, limit);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Word(
                rs.getInt("id"),
                rs.getString("word"),
                rs.getString("category"),
                rs.getInt("frequency")
            ));
        }
    } catch (SQLException e) { 
        System.err.println("[WordRepository] searchByPrefix error: " + e.getMessage());
    }
    return list;
}

// Add this method - get all unique words as list (for fuzzy matching)
public List<String> getAllWordStrings() {
    List<String> words = new ArrayList<>();
    String sql = "SELECT word FROM words";
    try (Connection c = DBConnection.getConnection();
         Statement s = c.createStatement();
         ResultSet rs = s.executeQuery(sql)) {
        while (rs.next()) {
            words.add(rs.getString("word"));
        }
    } catch (SQLException e) {
        System.err.println("[WordRepository] getAllWordStrings error: " + e.getMessage());
    }
    return words;
}

    public int getFrequency(String word) {
        String sql = "SELECT frequency FROM words WHERE LOWER(word) = LOWER(?)";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, word);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("frequency");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
