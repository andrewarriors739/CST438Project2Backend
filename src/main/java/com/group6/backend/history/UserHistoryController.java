// src/main/java/com/group6/backend/history/UserHistoryController.java
package com.group6.backend.history;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/history")
public class UserHistoryController {

  private final UserWordHistoryRepo history;

  public UserHistoryController(UserWordHistoryRepo history) {
    this.history = history;
  }

  @PutMapping("/{wordId}")
  public ResponseEntity<?> putHistory(@PathVariable Long userId, @PathVariable Long wordId) {
    if (history.existsByUserIdAndWordId(userId, wordId)) {
      // already present -> idempotent success
      return ResponseEntity.noContent().build(); // 204
    }
    UserWordHistory row = new UserWordHistory();
    row.setUserId(userId);
    row.setWordId(wordId);
    history.save(row);
    return ResponseEntity.status(201).build(); // 201 Created
  }

  @DeleteMapping("/{wordId}")
  public ResponseEntity<?> deleteHistory(@PathVariable Long userId, @PathVariable Long wordId) {
    history.deleteByUserIdAndWordId(userId, wordId);
    return ResponseEntity.noContent().build(); // 204
  }
}
