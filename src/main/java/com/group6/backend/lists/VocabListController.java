// src/main/java/com/group6/backend/lists/VocabListController.java
package com.group6.backend.lists;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/lists")
public class VocabListController {
  private final VocabListRepo repo;
  public VocabListController(VocabListRepo repo){ this.repo = repo; }

  @GetMapping
  public List<VocabList> all(@PathVariable Long userId){
    return repo.findByUserIdOrderByIdAsc(userId);
  }

  @PostMapping
  public ResponseEntity<?> create(@PathVariable Long userId, @RequestBody CreateList req){
    if (req.name()==null || req.name().isBlank()) return ResponseEntity.badRequest().body("name required");
    if (repo.existsByUserIdAndNameIgnoreCase(userId, req.name())) return ResponseEntity.status(409).body("list exists");
    var l=new VocabList(); l.setUserId(userId); l.setName(req.name()); var saved=repo.save(l);
    return ResponseEntity.created(URI.create("/api/users/%d/lists/%d".formatted(userId, saved.getId()))).body(saved);
  }

  public static record CreateList(String name) {}
}
