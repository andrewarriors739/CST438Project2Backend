package com.group6.backend.controller;

import com.group6.backend.model.Word;
import com.group6.backend.repo.WordsRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    
    
    
    
    
    
    
    //  POST new word using WordsRepo
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
}
