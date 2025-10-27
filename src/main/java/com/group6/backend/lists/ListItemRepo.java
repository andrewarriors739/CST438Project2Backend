// src/main/java/com/group6/backend/lists/ListItemRepo.java
package com.group6.backend.lists;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ListItemRepo extends JpaRepository<ListItem, Long> {
  boolean existsByListIdAndWordId(Long listId, Long wordId);
  long deleteByListIdAndWordId(Long listId, Long wordId);
  List<ListItem> findByListIdOrderByIdAsc(Long listId);
}
