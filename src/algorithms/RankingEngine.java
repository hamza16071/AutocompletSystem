package algorithms;

import models.Word;
import java.util.List;

/**
 * Ranks autocomplete results using a multi-factor score:
 * 1. Exact prefix match bonus
 * 2. Frequency (popularity)
 * 3. Shorter words ranked higher for same frequency
 */
public class RankingEngine {

    public static List<Word> rank(List<Word> words, String query) {
        final String q = query.toLowerCase().trim();

        words.sort((a, b) -> {
            int scoreA = score(a, q);
            int scoreB = score(b, q);
            return scoreB - scoreA;
        });

        return words;
    }

    private static int score(Word w, String query) {
        int s = w.getFrequency();                     // base: frequency

        String wl = w.getWord().toLowerCase();

        if (wl.equals(query))              s += 2000; // exact match
        else if (wl.startsWith(query))     s += 1000; // starts with query
        else if (wl.contains(" " + query)) s +=  500; // word boundary

        s -= w.getWord().length();                    // shorter words slightly preferred

        return s;
    }
}
