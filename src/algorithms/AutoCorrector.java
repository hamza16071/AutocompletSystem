package algorithms;

import services.FuzzyMatcher;
import database.WordRepository;
import models.Word;
import trie.Trie;
import java.util.*;

public class AutoCorrector {
    private WordRepository repo;
    private Trie trie;  // optional fallback
    private List<String> dictionary;
    private boolean useTrieFallback = true;

    public AutoCorrector(WordRepository repo) {
        this.repo = repo;
        this.trie = null;
        loadDictionary();
    }
    
    // Overloaded constructor if you want to use Trie as fallback
    public AutoCorrector(WordRepository repo, Trie trie) {
        this.repo = repo;
        this.trie = trie;
        loadDictionary();
    }
    
    private void loadDictionary() {
        dictionary = new ArrayList<>();
        try {
            List<Word> words = repo.getAllWords();
            for (Word w : words) {
                dictionary.add(w.getWord().toLowerCase());
            }
            System.out.println("[AutoCorrector] Loaded " + dictionary.size() + " words from DB");
        } catch (Exception e) {
            System.err.println("[AutoCorrector] DB load error: " + e.getMessage());
        }
        
        // If DB empty and trie available, load from trie
        if (dictionary.isEmpty() && trie != null && useTrieFallback) {
            // Trie se words collect karna hoga – Trie mein getAlWords method nahi hai.
            // Is liye ek helper method chahiye. Yahan simple solution: 
            // Tum Trie mein ek getAllWords() method bana sakte ho.
            // Abhi ke liye, hum fallback words ko manual daal dete hain (jaise AutocompleteService mein).
            System.out.println("[AutoCorrector] DB empty, using fallback words");
            String[][] fallback = {
                {"salam","200"}, {"hello","200"}, {"hi","195"}, {"how are you","190"},
                {"aap","200"}, {"main","200"}, {"kya","200"}, {"thank you","192"},
                {"Pakistan","185"}, {"karachi","180"}, {"lahore","178"}, {"islamabad","175"}
            };
            for (String[] w : fallback) {
                dictionary.add(w[0].toLowerCase());
            }
            System.out.println("[AutoCorrector] Loaded " + dictionary.size() + " fallback words");
        }
    }
    
    public boolean isCorrect(String word) {
        if (word == null || word.isEmpty()) return false;
        return dictionary.contains(word.toLowerCase());
    }
    
    public String getCorrection(String query) {
        if (query == null || query.trim().isEmpty()) return null;
        String q = query.trim().toLowerCase();
        
        // Already correct?
        if (dictionary.contains(q)) return null;
        
        // Find closest using FuzzyMatcher
        List<String> close = FuzzyMatcher.getClosest(q, dictionary, 1);
        if (!close.isEmpty()) {
            return close.get(0);
        }
        
        return null;
    }
}