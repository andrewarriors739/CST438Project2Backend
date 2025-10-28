// src/main/java/com/group6/backend/lists/ListItemsController.java
package com.group6.backend.lists;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.backend.model.Word;
import com.group6.backend.repo.WordsRepo;

@RestController
@RequestMapping("/api/users/{userId}/lists/{listId}/items")
public class ListItemsController {
  private final ListItemRepo items;
  private final WordsRepo words;
  private final VocabListRepo lists;   // add

  public ListItemsController(ListItemRepo items, WordsRepo words, VocabListRepo lists){
    this.items=items; this.words=words; this.lists=lists;
  }

  private boolean listOwnedByUser(Long userId, Long listId){
    return lists.findById(listId).map(l -> l.getUserId().equals(userId)).orElse(false);
  }

  @GetMapping
  public ResponseEntity<?> list(@PathVariable Long userId, @PathVariable Long listId){
    if (!listOwnedByUser(userId, listId)) return ResponseEntity.status(404).body("list not found");
    var rows = items.findByListIdOrderByIdAsc(listId);
    var ids  = rows.stream().map(ListItem::getWordId).toList();
    var wordsList = ids.isEmpty()? List.<Word>of() : words.findAllById(ids);
    return ResponseEntity.ok(wordsList);
  }

  @PutMapping("/{wordId}")
  public ResponseEntity<?> add(@PathVariable Long userId, @PathVariable Long listId, @PathVariable Long wordId) {
    if (!listOwnedByUser(userId, listId)) return ResponseEntity.status(404).body("list not found");
    if (!words.existsById(wordId)) return ResponseEntity.status(404).body("word not found");
    if (items.existsByListIdAndWordId(listId, wordId)) return ResponseEntity.noContent().build();
    var li = new ListItem(); li.setListId(listId); li.setWordId(wordId); items.save(li);
    return ResponseEntity.status(201).build();
  }

  @DeleteMapping("/{wordId}")
  public ResponseEntity<?> remove(@PathVariable Long userId, @PathVariable Long listId, @PathVariable Long wordId){
    if (!listOwnedByUser(userId, listId)) return ResponseEntity.status(404).body("list not found");
    items.deleteByListIdAndWordId(listId, wordId);
    return ResponseEntity.noContent().build();
  }
}
