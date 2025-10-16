package com.group6.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Used to test the WordRepo, we will use Vocab.java once it is finished instead

@Entity
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String term;
    private String definition;

    public Word() {}

    public Word(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    // getters and setters
    public Long getId() { return id; }

    public String getTerm() { return term; }

  
    
}
