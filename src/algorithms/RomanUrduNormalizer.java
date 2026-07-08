package algorithms;

/**
 * Normalises Roman-Urdu input before trie lookup.
 * Handles common spelling variants without changing meaning.
 */
public class RomanUrduNormalizer {

    /**
     * Lowercase, trim, collapse whitespace, and apply common
     * Roman-Urdu variant mappings so both spellings hit the same prefix.
     */
    public static String normalize(String input) {
        if (input == null) return "";

        String s = input.toLowerCase().trim();

        // Collapse multiple spaces
        s = s.replaceAll("\\s+", " ");

        // Common Roman-Urdu variant normalizations
        s = s.replace("ph", "f");     // phone → fone (optional; comment out if unwanted)
        s = s.replace("ch", "ch");    // no-op placeholder – keep 'ch' as-is
        s = s.replace("kh", "kh");    // same

        return s;
    }

    /** Split input into individual tokens for multi-word prefix matching. */
    public static String[] tokenize(String input) {
        if (input == null || input.trim().isEmpty()) return new String[0];
        return input.trim().split("\\s+");
    }
}
