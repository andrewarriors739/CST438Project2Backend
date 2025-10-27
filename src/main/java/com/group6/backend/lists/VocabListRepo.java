// src/main/java/com/group6/backend/lists/VocabListRepo.java
package com.group6.backend.lists;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabListRepo extends JpaRepository<VocabList, Long> {
  List<VocabList> findByUserIdOrderByIdAsc(Long userId);
  boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
}
