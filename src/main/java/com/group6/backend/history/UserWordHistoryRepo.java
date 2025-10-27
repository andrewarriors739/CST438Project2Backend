// src/main/java/com/group6/backend/history/UserWordHistoryRepo.java
package com.group6.backend.history;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWordHistoryRepo extends JpaRepository<UserWordHistory, Long> {
  boolean existsByUserIdAndWordId(Long userId, Long wordId);
  Optional<UserWordHistory> findByUserIdAndWordId(Long userId, Long wordId);
  long deleteByUserIdAndWordId(Long userId, Long wordId);
}
