package app;

import database.WordRepository;
import database.HistoryRepository;
import services.AutocompleteService;
import ui.MainFrame;
import utils.CSVLoader;

import javax.swing.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {

        // ── Working directory detect karo ─────────────────────────────
        String projectRoot = System.getProperty("user.dir");
        System.out.println("[Main] Working directory: " + projectRoot);

        // CSV ka absolute path
        String csvPath = projectRoot + File.separator
                + "src" + File.separator
                + "dataset" + File.separator
                + "roman_urdu_words.csv";

        System.out.println("[Main] CSV path: " + csvPath);

        // ── 1. DB table create karo ───────────────────────────────────
        WordRepository repo = new WordRepository();
        repo.createTable();

        // NEW: Create history table
        HistoryRepository historyRepo = new HistoryRepository();
        historyRepo.createTable();

        // ── 2. CSV load karo agar DB empty hai ───────────────────────
        if (repo.getWordCount() == 0) {
            System.out.println("[Main] Database empty. Loading CSV...");
            File csvFile = new File(csvPath);
            if (csvFile.exists()) {
                CSVLoader loader = new CSVLoader();
                int loaded = loader.loadFromCSV(csvPath);
                System.out.println("[Main] Loaded " + loaded + " words.");
            } else {
                System.err.println("[Main] CSV file nahi mila: " + csvPath);
                System.err.println("[Main] Manual insert try karo DB Browser se.");
            }
        } else {
            System.out.println("[Main] Database mein " + repo.getWordCount() + " words hain.");
        }

        // ── 3. Autocomplete service start karo ───────────────────────
        AutocompleteService service = new AutocompleteService();

        // ── 4. GUI launch karo ───────────────────────────────────────
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new MainFrame(service);
        });
    }
}