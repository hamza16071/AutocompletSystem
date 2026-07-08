package services;

import database.HistoryRepository;
import java.util.List;

public class SearchHistoryService {
    private HistoryRepository repo;

    public SearchHistoryService() {
        repo = new HistoryRepository();
        repo.createTable();
    }

    public void addToHistory(String word) {
        repo.addToHistory(word);
    }

    public List<String> getRecentHistory(int limit) {
        return repo.getRecentHistory(limit);
    }
    
    public void clearHistory() {
        repo.clearHistory();
    }
}