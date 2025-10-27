// src/main/java/com/group6/backend/history/UserWordHistory.java
package com.group6.backend.history;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_word_history",
       uniqueConstraints = @UniqueConstraint(name = "uniq_user_word", columnNames = {"user_id","word_id"}))
public class UserWordHistory {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "word_id", nullable = false)
  private Long wordId;

  @Column(name = "created_at", columnDefinition = "DATETIME(6)")
  private Instant createdAt = Instant.now();

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
    public Long getWordId() {
        return wordId;  
    }
    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
