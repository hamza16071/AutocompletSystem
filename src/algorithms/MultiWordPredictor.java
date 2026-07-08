package algorithms;

import database.WordRepository;
import models.Word;
import java.util.*;

public class MultiWordPredictor {
    private WordRepository repo;

    public MultiWordPredictor(WordRepository repo) {
        this.repo = repo;
    }

    /**
     * Given last word, predict next possible words
     * Example: "how" -> ["are", "to", "is"]
     */
    public List<String> predictNext(String lastWord, int limit) {
        if (lastWord == null || lastWord.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String prefix = lastWord.trim().toLowerCase() + " ";
        List<Word> matches = repo.searchByPrefix(prefix, limit * 3);
        
        Set<String> nextWords = new LinkedHashSet<>();
        for (Word w : matches) {
            String word = w.getWord().toLowerCase();
            if (word.startsWith(prefix)) {
                String rest = word.substring(prefix.length());
                // Take first word after the prefix
                if (!rest.isEmpty()) {
                    String next = rest.split(" ")[0];
                    nextWords.add(next);
                    if (nextWords.size() >= limit) break;
                }
            }
        }
        
        return new ArrayList<>(nextWords);
    }
    
    /**
     * Get full phrase suggestions (multi-word)
     */
    public List<String> getPhraseSuggestions(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Word> matches = repo.searchByPrefix(prefix.trim(), limit);
        List<String> result = new ArrayList<>();
        for (Word w : matches) {
            result.add(w.getWord());
        }
        return result;
    }
}