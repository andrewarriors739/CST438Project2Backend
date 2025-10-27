// src/main/java/com/group6/backend/lists/ListItem.java
package com.group6.backend.lists;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="list_items",
  uniqueConstraints=@UniqueConstraint(name="uniq_list_word", columnNames={"list_id","word_id"}))
public class ListItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

  @Column(name="list_id", nullable=false) Long listId;
  @Column(name="word_id", nullable=false) Long wordId;

  @Column(columnDefinition="DATETIME(6)") Instant createdAt = Instant.now();

  // getters/setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getListId() {
        return listId;
    }
    public void setListId(Long listId) {
        this.listId = listId;
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
