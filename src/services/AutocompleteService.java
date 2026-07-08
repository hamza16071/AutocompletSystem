package services;

import algorithms.RankingEngine;
import algorithms.MultiWordPredictor;
import algorithms.AutoCorrector;
import database.WordRepository;
import models.Word;
import trie.Trie;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteService {

    private Trie           trie;
    private WordRepository repo;
    private SearchHistoryService historyService;
    private MultiWordPredictor predictor;
    private AutoCorrector corrector;

    public AutocompleteService() {
        repo = new WordRepository();
        historyService = new SearchHistoryService();
        predictor = new MultiWordPredictor(repo);
        corrector = new AutoCorrector(repo);
        reload();
    }

    // ── Load all words from DB into Trie ──────────────────────────────
    public void reload() {
        trie = new Trie();

        List<Word> all = new ArrayList<>();
        try {
            all = repo.getAllWords();
        } catch (Exception e) {
            System.err.println("[AutocompleteService] DB error: " + e.getMessage());
        }

        if (all.isEmpty()) {
            System.err.println("[AutocompleteService] WARNING: DB empty! Loading fallback words...");
            loadFallbackWords();
        } else {
            for (Word w : all) {
                trie.insert(w.getWord(), w.getFrequency());
            }
            System.out.println("[AutocompleteService] Trie mein " + all.size() + " words load hue.");
        }
    }

    // ── Hardcoded fallback (agar DB kaam na kare) ────────────────────
    private void loadFallbackWords() {
        String[][] words = {
            {"aap", "200"}, {"aap kaise hain", "190"}, {"aap theek hain", "180"},
            {"main", "200"}, {"main theek hun", "195"}, {"mujhe", "195"},
            {"kya", "200"}, {"kya haal hai", "195"}, {"salam", "200"},
            {"hello", "200"}, {"hi", "195"}, {"thank you", "192"},
            {"reply karo", "185"}, {"chill kar", "188"}, {"Pakistan zindabad", "185"}
        };
        for (String[] w : words) {
            trie.insert(w[0], Integer.parseInt(w[1]));
        }
        System.out.println("[AutocompleteService] Fallback: " + words.length + " words loaded.");
    }

    // ── Get suggestions (with fuzzy + multi-word) ────────────────────
    public List<String> getSuggestions(String query, int limit) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();

        String q = query.trim();
        System.out.println("[AutocompleteService] Searching: '" + q + "'");

        // 1. Trie search
        List<Word> hits = new ArrayList<>();
        try {
            hits = trie.search(q, limit * 3);
        } catch (Exception e) {
            System.err.println("[AutocompleteService] Trie error: " + e.getMessage());
        }

        // 2. DB fallback agar trie empty ho
        if (hits.isEmpty()) {
            try {
                hits = repo.searchByPrefix(q, limit);
                System.out.println("[AutocompleteService] DB fallback results: " + hits.size());
            } catch (Exception e) {
                System.err.println("[AutocompleteService] DB fallback error: " + e.getMessage());
            }
        }

        List<Word> ranked = RankingEngine.rank(hits, q);
        List<String> results = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, ranked.size()); i++) {
            results.add(ranked.get(i).getWord());
        }

        // 3. Fuzzy fallback if results are too few
        if (results.size() < limit) {
            try {
                List<String> allWords = repo.getAllWordStrings();
                List<String> fuzzyMatches = FuzzyMatcher.getClosest(q, allWords, limit - results.size());
                for (String match : fuzzyMatches) {
                    if (!results.contains(match)) {
                        results.add(match);
                    }
                }
            } catch (Exception e) {
                System.err.println("[AutocompleteService] Fuzzy fallback error: " + e.getMessage());
            }
        }

        // 4. Multi-word prediction if query has space
        if (q.contains(" ")) {
            String[] parts = q.split(" ");
            String lastWord = parts[parts.length - 1];
            List<String> nextWords = predictor.predictNext(lastWord, 3);
            for (String next : nextWords) {
                String full = q + " " + next;
                if (!results.contains(full) && results.size() < limit * 2) {
                    results.add(full);
                }
            }
        }

        return results.subList(0, Math.min(limit, results.size()));
    }

    // ── Record user selection (frequency boost + history) ────────────
    public void recordSearch(String word) {
        if (word == null || word.trim().isEmpty()) return;
        try {
            repo.incrementFrequency(word.trim());
            int newFreq = repo.getFrequency(word.trim());
            trie.updateFrequency(word.trim(), newFreq);
            historyService.addToHistory(word.trim());   // ADD TO HISTORY
        } catch (Exception e) {
            System.err.println("[AutocompleteService] recordSearch error: " + e.getMessage());
        }
    }

    // ── Add new word ──────────────────────────────────────────────────
    public void addWord(String word, String category, int frequency) {
        Word w = new Word(0, word.trim(), category, frequency);
        try { repo.insertWord(w); } catch (Exception ignored) {}
        trie.insert(word.trim(), frequency);
    }

    // ── Word count ────────────────────────────────────────────────────
    public int getTotalWords() {
        try {
            int dbCount = repo.getWordCount();
            return dbCount > 0 ? dbCount : trie.getSize();
        } catch (Exception e) {
            return trie.getSize();
        }
    }

    // ═══════════════════ NEW FEATURES ═══════════════════

    // Search History
    public List<String> getSearchHistory(int limit) {
        return historyService.getRecentHistory(limit);
    }
    public void clearHistory() {
        historyService.clearHistory();
    }

    // Multi-word prediction
    public List<String> getNextWordSuggestions(String lastWord, int limit) {
        return predictor.predictNext(lastWord, limit);
    }
    public List<String> getPhraseSuggestions(String prefix, int limit) {
        return predictor.getPhraseSuggestions(prefix, limit);
    }

    // Auto-correction
    public String getCorrection(String query) {
        return corrector.getCorrection(query);
    }
    public boolean isWordCorrect(String word) {
        return corrector.isCorrect(word);
    }
}