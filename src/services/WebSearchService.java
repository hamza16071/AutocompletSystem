package services;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebSearchService {
    
    public static void searchOnGoogle(String query) {
        if (query == null || query.trim().isEmpty()) return;
        
        try {
            String encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.toString());
            String url = "https://www.google.com/search?q=" + encodedQuery;
            Desktop.getDesktop().browse(new URI(url));
            System.out.println("[WebSearchService] Opening: " + url);
        } catch (Exception e) {
            System.err.println("[WebSearchService] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void searchOnGoogleWithFallback(String query) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            searchOnGoogle(query);
        } else {
            System.err.println("[WebSearchService] Desktop browsing not supported");
            // Fallback: show URL in console
            System.out.println("Search URL: https://www.google.com/search?q=" + 
                URLEncoder.encode(query, StandardCharsets.UTF_8));
        }
    }
}