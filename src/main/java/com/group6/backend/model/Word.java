package com.group6.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

// Used to test the WordRepo, we will use Vocab.java once it is finished instead

@Entity
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String term;
   
    @Lob
  @Column(name = "definition", columnDefinition = "TEXT", nullable = false)
    private String definition;

    public Word() {}

    public Word(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

   // getters
   public Long getId() { return id; }
   public String getTerm() { return term; }
   public String getDefinition() { return definition; }

   // setters (needed for JSON binding)
   public void setTerm(String term) { this.term = term; }
   public void setDefinition(String definition) { this.definition = definition; }
}
    

