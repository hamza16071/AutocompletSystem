package utils;

import database.WordRepository;
import models.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {

    /**
     * CSV file se words padh kar database mein insert karta hai.
     * CSV format: word,category,frequency
     * Example line: salam,roman_urdu,1500
     */
    public int loadFromCSV(String csvFilePath) {
        List<Word> words = new ArrayList<>();
        int loadedCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Agar pehli line header hai to skip karo (optional)
                if (isFirstLine && line.toLowerCase().contains("word")) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;

                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 3) {
                    System.err.println("[CSVLoader] Skip invalid line: " + line);
                    continue;
                }

                String word = parts[0].trim();
                String category = parts[1].trim();
                int frequency;

                try {
                    frequency = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    frequency = 1; // default frequency
                }

                words.add(new Word(0, word, category, frequency));
            }

            // Batch insert karo
            if (!words.isEmpty()) {
                WordRepository repo = new WordRepository();
                repo.insertBatch(words);
                loadedCount = words.size();
                System.out.println("[CSVLoader] " + loadedCount + " words successfully loaded.");
            }

        } catch (IOException e) {
            System.err.println("[CSVLoader] Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }

        return loadedCount;
    }
}