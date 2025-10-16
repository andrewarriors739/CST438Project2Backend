package com.group6.backend.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VocabTest {

    @Autowired
    private TestEntityManager entityManager;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // Given & When
        Vocab vocab = new Vocab();

        // Then
        assertNotNull(vocab);
        assertNull(vocab.getId());
        assertNull(vocab.getWord());
        assertNull(vocab.getDefinition());
        assertNotNull(vocab.getCreatedAt());
        assertNotNull(vocab.getUpdatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String word = "test";
        String definition = "a procedure intended to establish the quality";

        // When
        Vocab vocab = new Vocab(word, definition);

        // Then
        assertNotNull(vocab);
        assertEquals(word, vocab.getWord());
        assertEquals(definition, vocab.getDefinition());
        assertNotNull(vocab.getCreatedAt());
        assertNotNull(vocab.getUpdatedAt());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Vocab vocab = new Vocab();
        Long id = 1L;
        String word = "example";
        String definition = "a thing characteristic of its kind";
        LocalDateTime now = LocalDateTime.now();

        // When
        vocab.setId(id);
        vocab.setWord(word);
        vocab.setDefinition(definition);
        vocab.setCreatedAt(now);
        vocab.setUpdatedAt(now);

        // Then
        assertEquals(id, vocab.getId());
        assertEquals(word, vocab.getWord());
        assertEquals(definition, vocab.getDefinition());
        assertEquals(now, vocab.getCreatedAt());
        assertEquals(now, vocab.getUpdatedAt());
    }

    @Test
    void testSetWordUpdatesTimestamp() {
        // Given
        Vocab vocab = new Vocab();
        LocalDateTime originalTime = vocab.getUpdatedAt();
        
        // Wait a small amount to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        vocab.setWord("newWord");

        // Then
        assertTrue(vocab.getUpdatedAt().isAfter(originalTime));
    }

    @Test
    void testSetDefinitionUpdatesTimestamp() {
        // Given
        Vocab vocab = new Vocab();
        LocalDateTime originalTime = vocab.getUpdatedAt();
        
        // Wait a small amount to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        vocab.setDefinition("newDefinition");

        // Then
        assertTrue(vocab.getUpdatedAt().isAfter(originalTime));
    }

    @Test
    void testValidVocab() {
        // Given
        Vocab vocab = new Vocab("valid", "a valid definition");

        // When
        Set<ConstraintViolation<Vocab>> violations = validator.validate(vocab);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testWordCannotBeBlank() {
        // Given
        Vocab vocab = new Vocab();
        vocab.setWord("");
        vocab.setDefinition("valid definition");

        // When
        Set<ConstraintViolation<Vocab>> violations = validator.validate(vocab);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("word") &&
                        v.getMessage().contains("cannot be blank")));
    }

    @Test
    void testWordCannotBeNull() {
        // Given
        Vocab vocab = new Vocab();
        vocab.setWord(null);
        vocab.setDefinition("valid definition");

        // When
        Set<ConstraintViolation<Vocab>> violations = validator.validate(vocab);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("word") &&
                        v.getMessage().contains("cannot be blank")));
    }

    @Test
    void testWordSizeLimit() {
        // Given
        String longWord = "a".repeat(101); // 101 characters
        Vocab vocab = new Vocab();
        vocab.setWord(longWord);
        vocab.setDefinition("valid definition");

        // When
        Set<ConstraintViolation<Vocab>> violations = validator.validate(vocab);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("word") &&
                        v.getMessage().contains("cannot exceed 100 characters")));
    }

    @Test
    void testDefinitionCannotBeBlank() {
        // Given
        Vocab vocab = new Vocab();
        vocab.setWord("valid");
        vocab.setDefinition("");

        // When
        Set<ConstraintViolation<Vocab>> violations = validator.validate(vocab);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("definition") &&
                        v.getMessage().contains("cannot be blank")));
    }

    @Test
    void testDefinitionCannotBeNull() {
        // Given
        Vocab vocab = new Vocab();
        vocab.setWord("valid");
        vocab.setDefinition(null);

        // When
        Set<ConstraintViolation<Vocab>> violations = validator.validate(vocab);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("definition") &&
                        v.getMessage().contains("cannot be blank")));
    }

    @Test
    void testDefinitionSizeLimit() {
        // Given
        String longDefinition = "a".repeat(501); // 501 characters
        Vocab vocab = new Vocab();
        vocab.setWord("valid");
        vocab.setDefinition(longDefinition);

        // When
        Set<ConstraintViolation<Vocab>> violations = validator.validate(vocab);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("definition") &&
                        v.getMessage().contains("cannot exceed 500 characters")));
    }

    @Test
    void testJpaPersistence() {
        // Given
        Vocab vocab = new Vocab("persistence", "the ability to persist data");

        // When
        Vocab savedVocab = entityManager.persistAndFlush(vocab);
        entityManager.clear();

        // Then
        assertNotNull(savedVocab.getId());
        assertEquals("persistence", savedVocab.getWord());
        assertEquals("the ability to persist data", savedVocab.getDefinition());
        assertNotNull(savedVocab.getCreatedAt());
        assertNotNull(savedVocab.getUpdatedAt());
    }

    @Test
    void testJpaRetrieval() {
        // Given
        Vocab vocab = new Vocab("retrieval", "the process of getting data back");
        Vocab savedVocab = entityManager.persistAndFlush(vocab);
        Long id = savedVocab.getId();
        entityManager.clear();

        // When
        Vocab foundVocab = entityManager.find(Vocab.class, id);

        // Then
        assertNotNull(foundVocab);
        assertEquals(id, foundVocab.getId());
        assertEquals("retrieval", foundVocab.getWord());
        assertEquals("the process of getting data back", foundVocab.getDefinition());
    }

    @Test
    void testToString() {
        // Given
        Vocab vocab = new Vocab("toString", "method that returns string representation");

        // When
        String result = vocab.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Vocab"));
        assertTrue(result.contains("toString"));
        assertTrue(result.contains("method that returns string representation"));
    }

    @Test
    void testPrePersistCallback() {
        // Given
        Vocab vocab = new Vocab("callback", "a function called at a specific time");

        // When
        Vocab savedVocab = entityManager.persistAndFlush(vocab);

        // Then
        assertNotNull(savedVocab.getCreatedAt());
        assertNotNull(savedVocab.getUpdatedAt());
        assertEquals(savedVocab.getCreatedAt(), savedVocab.getUpdatedAt());
    }

    @Test
    void testPreUpdateCallback() {
        // Given
        Vocab vocab = new Vocab("update", "to make something more recent");
        Vocab savedVocab = entityManager.persistAndFlush(vocab);
        LocalDateTime originalUpdatedAt = savedVocab.getUpdatedAt();
        
        // Wait to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        savedVocab.setWord("updated");
        Vocab updatedVocab = entityManager.persistAndFlush(savedVocab);

        // Then
        assertTrue(updatedVocab.getUpdatedAt().isAfter(originalUpdatedAt));
    }
}
