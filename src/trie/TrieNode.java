package trie;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    public Map<Character, TrieNode> children;
    public boolean isEndOfWord;
    public String  fullWord;
    public int     frequency;

    public TrieNode() {
        children    = new HashMap<>();
        isEndOfWord = false;
        fullWord    = null;
        frequency   = 0;
    }
}
