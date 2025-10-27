// src/main/java/com/group6/backend/lists/ListItemsController.java
package com.group6.backend.lists;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.backend.model.Word;
import com.group6.backend.repo.WordsRepo;

@RestController
@RequestMapping("/api/users/{userId}/lists/{listId}/items")
public class ListItemsController {
  private final ListItemRepo items;
  private final WordsRepo words;

  public ListItemsController(ListItemRepo items, WordsRepo words){
    this.items=items; this.words=words;
  }

  // Return actual Word objects for easy UI
  @GetMapping
  public List<Word> list(@PathVariable Long listId){
    var rows = items.findByListIdOrderByIdAsc(listId);
    var ids = rows.stream().map(ListItem::getWordId).toList();
    return ids.isEmpty()? List.of() : words.findAllById(ids);
  }

  @PostMapping
  public ResponseEntity<?> add(@PathVariable Long listId, @RequestBody AddReq body){
    if (body.wordId()==null) return ResponseEntity.badRequest().body("wordId required");
    if (!words.existsById(body.wordId())) return ResponseEntity.status(404).body("word not found");
    if (items.existsByListIdAndWordId(listId, body.wordId())) return ResponseEntity.noContent().build();
    var li=new ListItem(); li.setListId(listId); li.setWordId(body.wordId()); items.save(li);
    return ResponseEntity.status(201).build();
  }

  @DeleteMapping("/{wordId}")
  public ResponseEntity<?> remove(@PathVariable Long listId, @PathVariable Long wordId){
    items.deleteByListIdAndWordId(listId, wordId);
    return ResponseEntity.noContent().build();
  }

  public static record AddReq(Long wordId) {}
}
