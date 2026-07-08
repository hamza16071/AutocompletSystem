package services;

import java.util.*;

public class FuzzyMatcher {
    
    public static int editDistance(String a, String b) {
        if (a == null || b == null) return -1;
        int m = a.length(), n = b.length();
        int[][] dp = new int[m+1][n+1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (a.charAt(i-1) == b.charAt(j-1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i-1][j]+1, dp[i][j-1]+1), dp[i-1][j-1]+cost);
            }
        }
        return dp[m][n];
    }

    public static List<String> getClosest(String query, List<String> candidates, int limit) {
        if (query == null || candidates == null || candidates.isEmpty()) return new ArrayList<>();
        String q = query.toLowerCase();
        List<Map.Entry<String, Integer>> scores = new ArrayList<>();
        for (String cand : candidates) {
            int dist = editDistance(q, cand.toLowerCase());
            scores.add(new AbstractMap.SimpleEntry<>(cand, dist));
        }
        scores.sort(Comparator.comparingInt(Map.Entry::getValue));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, scores.size()); i++) {
            result.add(scores.get(i).getKey());
        }
        return result;
    }
}