package com.group6.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;

//This entity file will represent a table we have in our database 
// it will also communicate with Hibernate to create the schema for our database

//Word table defined in entity file
@Entity
@Table(name = "words")
public class Vocab {

    //The word will need an ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //We will need to make sure that the word is a String and not left blank
    //We also need to set the size of the String for the word
    @NotBlank(message = "Word cannot be blank")
    @Size(max = 100, message = "Word cannot exceed 100 characters")
    @Column(name = "word", nullable = false, length = 100)
    private String word;

    //We also need to make sure that the definition of the word is a string and not blank
    //We also need to set the size of the String for the definition
    @NotBlank(message = "Definition cannot be blank")
    @Size(max = 500, message = "Definition cannot exceed 500 characters")
    @Column(name = "definition", nullable = false, length = 500)
    private String definition;

    //Timestamp fields for tracking creation and modification
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //Constructors
    public Vocab() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Vocab(String word, String definition) {
        this();
        this.word = word;
        this.definition = definition;
    }

    //Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    //JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Vocab{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", definition='" + definition + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}



