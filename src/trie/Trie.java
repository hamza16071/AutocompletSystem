package trie;

import models.Word;
import java.util.ArrayList;
import java.util.List;

public class Trie {

    private final TrieNode root;
    private int size = 0;

    public Trie() {
        root = new TrieNode();
    }

    /** Word insert karo frequency ke saath */
    public void insert(String word, int frequency) {
        if (word == null || word.isEmpty()) return;
        TrieNode node = root;
        for (char c : word.toLowerCase().toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        if (!node.isEndOfWord) size++;   // sirf naye word pe count badhao
        node.isEndOfWord = true;
        node.fullWord    = word;
        node.frequency   = frequency;
    }

    /** Prefix se words search karo, limit ke saath */
    public List<Word> search(String prefix, int limit) {
        if (prefix == null || prefix.isEmpty()) return new ArrayList<>();

        TrieNode node = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            if (!node.children.containsKey(c)) return new ArrayList<>();
            node = node.children.get(c);
        }

        List<Word> results = new ArrayList<>();
        dfs(node, results);

        results.sort((a, b) -> b.getFrequency() - a.getFrequency());
        return results.subList(0, Math.min(limit, results.size()));
    }

    /** DFS — saare words collect karo */
    private void dfs(TrieNode node, List<Word> results) {
        if (node.isEndOfWord) {
            results.add(new Word(0, node.fullWord, "", node.frequency));
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, results);
        }
    }

    /** Frequency update karo jab user select kare */
    public void updateFrequency(String word, int newFrequency) {
        TrieNode node = root;
        for (char c : word.toLowerCase().toCharArray()) {
            if (!node.children.containsKey(c)) return;
            node = node.children.get(c);
        }
        if (node.isEndOfWord) node.frequency = newFrequency;
    }

    /** Prefix exist karta hai? */
    public boolean hasPrefix(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            if (!node.children.containsKey(c)) return false;
            node = node.children.get(c);
        }
        return true;
    }

    /** Trie mein total words */
    public int getSize() {
        return size;
    }
}
