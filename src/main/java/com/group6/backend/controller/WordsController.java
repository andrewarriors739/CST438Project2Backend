package com.group6.backend.controller;

import com.group6.backend.model.Word;
import com.group6.backend.repo.WordsRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// Routes
@RestController
@RequestMapping("/api/words")
public class WordsController {

    private final WordsRepo repo;

    // Connected to WordsRepo
    public WordsController(WordsRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Word> getAllWords() {
        return repo.findAll();
    }

    // POST new word using WordsRepo
    @PostMapping
    public ResponseEntity<?> addWord(@RequestBody Word newWord) {
        if (newWord.getTerm() == null || newWord.getTerm().isBlank()) {
            return ResponseEntity.badRequest().body(" 'term' cannot be empty");
        }

        // Prevent duplicates
        if (repo.existsByTermIgnoreCase(newWord.getTerm())) {
            return ResponseEntity.status(409).body("⚠️ Word already exists!");
        }

        // Save to DB
        Word saved = repo.save(newWord);
        return ResponseEntity.ok(saved);
    }

    // GET word by term
    @GetMapping("/{term}")
    public ResponseEntity<?> getWordByTerm(@PathVariable String term) {
        Word word = repo.findByTermIgnoreCase(term);
        if (word == null) {
            return ResponseEntity.status(404).body("Word not found");
        }
        return ResponseEntity.ok(word);
    }

    // GET daily word (random word for simplicity)
    @GetMapping("/daily")
    public ResponseEntity<?> getDailyWord() {
        List<Word> words = repo.findAll();
        if (words.isEmpty()) {
            return ResponseEntity.status(404).body("No words available");
        }

        Word dailyWord = words.get(ThreadLocalRandom.current().nextInt(words.size()));

        return ResponseEntity.ok(dailyWord);
    }
}
