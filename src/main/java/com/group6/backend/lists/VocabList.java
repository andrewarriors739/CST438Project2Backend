// src/main/java/com/group6/backend/lists/VocabList.java
package com.group6.backend.lists;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name="vocab_lists")
public class VocabList {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

  @Column(nullable=false) Long userId;
  @Column(nullable=false, length=120) String name;

  @Column(columnDefinition="DATETIME(6)") Instant createdAt = Instant.now();

  // getters/setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
