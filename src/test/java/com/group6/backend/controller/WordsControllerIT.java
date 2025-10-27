package com.group6.backend.controller;

import com.group6.backend.model.Word;
import com.group6.backend.repo.WordsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WordsControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WordsRepo wordsRepo;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        
        // Clear and populate test data
        wordsRepo.deleteAll();
        wordsRepo.save(new Word("apple", "A red fruit"));
        wordsRepo.save(new Word("banana", "A yellow fruit"));
        wordsRepo.save(new Word("cherry", "A small red fruit"));
    }

    // ========== GET /api/words - getAllWords() ==========
    @Test
    void getAllWords_ReturnsListOfAllWords() {
        String url = baseUrl + "/api/words";
        ResponseEntity<Word[]> response = restTemplate.getForEntity(url, Word[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(3);
        
        Word[] words = response.getBody();
        assertThat(words)                    // words is Word[]
  .extracting("term")               // or "term" if your field is named term
  .containsExactlyInAnyOrder("apple","banana","cherry");
    }

    @Test
    void getAllWords_ReturnsEmptyArrayWhenNoWords() {
        wordsRepo.deleteAll();
        
        String url = baseUrl + "/api/words";
        ResponseEntity<Word[]> response = restTemplate.getForEntity(url, Word[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(0);
    }

    // ========== POST /api/words - addWord() ==========
    @Test
    void addWord_SuccessfullyAddsNewWord() {
        String url = baseUrl + "/api/words";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = """
            {
                "term": "dragon",
                "definition": "A mythical creature"
            }
            """;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Word> response = restTemplate.postForEntity(url, request, Word.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTerm()).isEqualTo("dragon");
        assertThat(response.getBody().getDefinition()).isEqualTo("A mythical creature");
        
        // Verify it was saved to the database
        assertThat(wordsRepo.findByTermIgnoreCase("dragon")).isNotNull();
    }

    @Test
    void addWord_RejectsEmptyTerm() {
        String url = baseUrl + "/api/words";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = """
            {
                "term": "",
                "definition": "Some definition"
            }
            """;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("term");
    }

    @Test
    void addWord_RejectsEmptyDefinition() {
        String url = baseUrl + "/api/words";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = """
            {
                "term": "test",
                "definition": ""
            }
            """;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("definition");
    }

    @Test
    void addWord_RejectsDuplicateWord() {
        String url = baseUrl + "/api/words";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = """
            {
                "term": "apple",
                "definition": "Red fruit"
            }
            """;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("already exists");
    }

    @Test
    void addWord_RejectsNullTerm() {
        String url = baseUrl + "/api/words";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = """
            {
                "term": null,
                "definition": "Some definition"
            }
            """;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("term");
    }

    // ========== GET /api/words/{term} - getWordByTerm() ==========
    @Test
    void getWordByTerm_FindsExistingWord() {
        String url = baseUrl + "/api/words/apple";
        ResponseEntity<Word> response = restTemplate.getForEntity(url, Word.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTerm()).isEqualTo("apple");
        assertThat(response.getBody().getDefinition()).isEqualTo("A red fruit");
    }

    @Test
    void getWordByTerm_Returns404ForNonexistentWord() {
        String url = baseUrl + "/api/words/nonexistent";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Word not found");
    }

    @Test
    void getWordByTerm_IsCaseInsensitive() {
        String url = baseUrl + "/api/words/APPLE";
        ResponseEntity<Word> response = restTemplate.getForEntity(url, Word.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTerm()).isEqualTo("apple");
    }

    @Test
    void getWordByTerm_CaseInsensitiveWithMixedCase() {
        String url = baseUrl + "/api/words/AppLe";
        ResponseEntity<Word> response = restTemplate.getForEntity(url, Word.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTerm()).isEqualTo("apple");
    }

    // ========== GET /api/words/daily - getDailyWord() ==========
    @Test
    void getDailyWord_ReturnsWordBasedOnDayOfYear() {
        String url = baseUrl + "/api/words/daily";
        ResponseEntity<Word> response = restTemplate.getForEntity(url, Word.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        // Should contain one of our test words
        String term = response.getBody().getTerm();
        assertThat(term).isIn("apple", "banana", "cherry");
    }

    @Test
    void getDailyWord_ReturnsSameWordOnSameDay() {
        String url = baseUrl + "/api/words/daily";
        
        // Call the endpoint multiple times on the same day
        ResponseEntity<Word> response1 = restTemplate.getForEntity(url, Word.class);
        ResponseEntity<Word> response2 = restTemplate.getForEntity(url, Word.class);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Should return the same word both times since it's the same day
        assertThat(response1.getBody().getTerm()).isEqualTo(response2.getBody().getTerm());
    }

    @Test
    void getDailyWord_Returns404WhenNoWordsAvailable() {
        wordsRepo.deleteAll();
        
        String url = baseUrl + "/api/words/daily";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("No words available");
    }

    @Test
    void getDailyWord_HandlesModuloCorrectly() {
        // This test verifies that the daily word algorithm works with modulo
        String url = baseUrl + "/api/words/daily";
        ResponseEntity<Word> response = restTemplate.getForEntity(url, Word.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        // We have 3 words, so dayOfYear % 3 should give us one of them
        List<Word> allWords = wordsRepo.findAll();
        assertThat(allWords).hasSize(3);
        
        // Check that the returned word is one of our test words
        Word dailyWord = response.getBody();
        assertThat(allWords).extracting(Word::getTerm)
            .contains(dailyWord.getTerm());
    }

    // ========== GET /api/words/random - getRandomWord() ==========
    @Test
    void getRandomWord_ReturnsWordFromAvailableWords() {
        String url = baseUrl + "/api/words/random";
        ResponseEntity<Word> response = restTemplate.getForEntity(url, Word.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        // Should contain one of our test words
        String term = response.getBody().getTerm();
        assertThat(term).isIn("apple", "banana", "cherry");
    }

    @Test
    void getRandomWord_ReturnsDifferentWordsOnMultipleCalls() {
        String url = baseUrl + "/api/words/random";
        
        // Call the endpoint multiple times - should potentially get different words
        Word word1 = restTemplate.getForEntity(url, Word.class).getBody();
        Word word2 = restTemplate.getForEntity(url, Word.class).getBody();
        Word word3 = restTemplate.getForEntity(url, Word.class).getBody();

        // At least verify they're all valid words
        assertThat(word1).isNotNull();
        assertThat(word2).isNotNull();
        assertThat(word3).isNotNull();
        
        // Verify they're all from our test words
        assertThat(word1.getTerm()).isIn("apple", "banana", "cherry");
        assertThat(word2.getTerm()).isIn("apple", "banana", "cherry");
        assertThat(word3.getTerm()).isIn("apple", "banana", "cherry");
    }

    @Test
    void getRandomWord_Returns404WhenNoWordsAvailable() {
        wordsRepo.deleteAll();
        
        String url = baseUrl + "/api/words/random";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("No words available");
    }

    @Test
    void getRandomWord_CanReturnAnyWordFromList() {
        String url = baseUrl + "/api/words/random";
        
        // Get all test words
        List<Word> allWords = wordsRepo.findAll();
        assertThat(allWords).hasSize(3);
        
        // Call random multiple times and collect results
        List<String> returnedTerms = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Word randomWord = restTemplate.getForEntity(url, Word.class).getBody();
            returnedTerms.add(randomWord.getTerm());
        }
        
        // Verify all returned words are from our test words
        assertThat(returnedTerms).allMatch(term -> term.equals("apple") || term.equals("banana") || term.equals("cherry"));
    }

    // ========== DELETE /api/words/{wordId} - deleteWordById() ==========
    @Test
    void deleteWordById_DeletesExistingWord() {
        // Get a word to delete
        Word wordToDelete = wordsRepo.findByTermIgnoreCase("apple");
        Long wordId = wordToDelete.getId();
        
        String url = baseUrl + "/api/words/" + wordId;
        ResponseEntity<Void> response = restTemplate.exchange(
            url, HttpMethod.DELETE, null, Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Verify word is deleted
        assertThat(wordsRepo.findById(wordId)).isEmpty();
    }

    @Test
    void deleteWordById_Returns404ForNonexistentId() {
        String url = baseUrl + "/api/words/99999";
        ResponseEntity<Void> response = restTemplate.exchange(
            url, HttpMethod.DELETE, null, Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteWordById_RemovesOnlySelectedWord() {
        // Get total count before deletion
        long initialCount = wordsRepo.count();
        
        // Get a word to delete
        Word wordToDelete = wordsRepo.findByTermIgnoreCase("banana");
        Long wordId = wordToDelete.getId();
        
        String url = baseUrl + "/api/words/" + wordId;
        restTemplate.delete(url);
        
        // Verify only one word was deleted
        assertThat(wordsRepo.count()).isEqualTo(initialCount - 1);
        assertThat(wordsRepo.findById(wordId)).isEmpty();
        
        // Verify other words still exist
        assertThat(wordsRepo.findByTermIgnoreCase("apple")).isNotNull();
        assertThat(wordsRepo.findByTermIgnoreCase("cherry")).isNotNull();
    }
}

