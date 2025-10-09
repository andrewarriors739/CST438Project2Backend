package com.group6.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

// CONTROLLER handles HTTP requests and returns responses
// Tested DB to make sure it was running properly
@RestController
public class TestDbController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/test-db")
    public Map<String,Object> testDb() {
      try {
        Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return Map.of("status","connected","ping", one); // returns 1
      } catch (Exception e) {
        return Map.of("status","error","message", e.getMessage());
      }
    }
    // Making sure it connects to the DB
    @GetMapping("/api/db-time")
public Map<String,Object> dbTime() {
  Map<String,Object> row = jdbcTemplate.queryForMap("SELECT CURRENT_TIMESTAMP() AS now_utc");
  return Map.of("status","connected","result", row);
}
}