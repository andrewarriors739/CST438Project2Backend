package com.group6.backend.repo;
import com.group6.backend.model.Word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repo is basically a database helper, used to query the database
@Repository
public interface WordsRepo extends JpaRepository<Word, Long> {
    boolean existsByTermIgnoreCase(String term);
    Word findByTermIgnoreCase(String term);

}