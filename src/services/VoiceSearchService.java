package services;

import javax.sound.sampled.*;
import org.vosk.Model;
import org.vosk.Recognizer;
import java.io.*;

public class VoiceSearchService {
    private Model model;
    private Recognizer recognizer;

    public VoiceSearchService() {
        try {
            // Model ka path (project ke src folder ke andar)
            String modelPath = "src/vosk-model-small-en-us-0.15";
            File modelDir = new File(modelPath);
            if (!modelDir.exists()) {
                System.err.println("[Voice] Model not found at " + modelPath);
                return;
            }
            model = new Model(modelPath);
            System.out.println("[Voice] Model loaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String listenOnce() {
        if (model == null) return "";

        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            recognizer = new Recognizer(model, 16000);
            byte[] buffer = new byte[4096];
            StringBuilder result = new StringBuilder();
            long startTime = System.currentTimeMillis();
            long maxListen = 5000; // 5 seconds

            while (System.currentTimeMillis() - startTime < maxListen) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                if (bytesRead > 0 && recognizer.acceptWaveForm(buffer, bytesRead)) {
                    String text = recognizer.getResult();
                    // Extract text from JSON
                    if (text.contains("\"text\"")) {
                        int start = text.indexOf("\"text\":\"") + 8;
                        int end = text.indexOf("\"", start);
                        if (start > 8 && end > start) {
                            result.append(text.substring(start, end)).append(" ");
                        }
                    }
                }
            }
            recognizer.close();
            line.close();
            return result.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void close() {
        if (recognizer != null) recognizer.close();
        if (model != null) model.close();
    }
}