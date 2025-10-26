package com.group6.backend.populate;

import com.group6.backend.model.Word;
import com.group6.backend.repo.WordsRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("populate")
public class WordPopulator implements CommandLineRunner {

    private final WordsRepo repo;
    private static final int BATCH_SIZE = 100;  // how many words to insert per batch
    private static final int LIMIT = 1000;      // total words to insert (adjust as needed)

    public WordPopulator(WordsRepo repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("📘 Starting WordPopulator...");

        // Load CSV from src/main/resources
        try (var inputStream = getClass().getResourceAsStream("/dictionary.csv");
             var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            if (inputStream == null) {
                System.err.println("dictionary.csv not found in resources folder!");
                return;
            }

            // Clear table before reseeding (optional)
            repo.deleteAll();
            System.out.println("🧹 Cleared existing words table.");

            List<Word> batch = new ArrayList<>();
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null && count < LIMIT) {
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                String term = parts[0].trim();
                String definition = parts[1].trim();

                // Avoid duplicates and empty terms
                if (!term.isEmpty() && !repo.existsByTermIgnoreCase(term)) {
                    batch.add(new Word(term, definition));
                    count++;
                }

                // Save in chunks for efficiency
                if (batch.size() >= BATCH_SIZE) {
                    repo.saveAll(batch);
                    batch.clear();
                }
            }

            // Save any remaining words
            if (!batch.isEmpty()) {
                repo.saveAll(batch);
            }

            System.out.println("Successfully inserted " + count + " unique words into the database!");
        }
    }
}
