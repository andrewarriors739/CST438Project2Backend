package com.group6.backend.controller;

import com.group6.backend.model.Word;
import com.group6.backend.repo.WordsRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    // POST new word using WordsRepo
    @PostMapping
    public ResponseEntity<?> addWord(@RequestBody Word newWord) {
        if (newWord.getTerm() == null || newWord.getTerm().isBlank()) {
            return ResponseEntity.badRequest().body(" 'term' cannot be empty");
        }
        if (newWord.getDefinition() == null || newWord.getDefinition().isBlank()) {
            return ResponseEntity.badRequest().body("'definition' cannot be empty");
        }
    

        // Prevent duplicates
        if (repo.existsByTermIgnoreCase(newWord.getTerm())) {
            return ResponseEntity.status(409).body("Word already exists!");
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

    // GET daily word based on the day of the year
    @GetMapping("/daily")
    public ResponseEntity<?> getDailyWord() {
        List<Word> words = repo.findAll();
        if (words.isEmpty()) {
            return ResponseEntity.status(404).body("No words available");
        }

        // Get the day of the year: 1-365 or 1-366 if leap year
        int dayOfYear = LocalDate.now().getDayOfYear();
        
        // Use % to go through available words based on the day of the year
        int wordIndex = dayOfYear % words.size();
        
        Word dailyWord = words.get(wordIndex);

        return ResponseEntity.ok(dailyWord);
    }
   @GetMapping("/random")
    public ResponseEntity<?> getRandomWord() {
        List<Word> words = repo.findAll();
        if (words.isEmpty()) {
            return ResponseEntity.status(404).body("No words available");
        }

        // Get a random word from the list 
        int i = (int) (Math.random() * words.size());
        Word randomWord = words.get(i);

        return ResponseEntity.ok(randomWord);
    }  
    // DELETE a word by its ID
    @DeleteMapping("/{wordId}")
    public ResponseEntity<Void> deleteWordById(@PathVariable Long wordId) {
        if (!repo.existsById(wordId)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(wordId);
        return ResponseEntity.ok().build();
    }
}
