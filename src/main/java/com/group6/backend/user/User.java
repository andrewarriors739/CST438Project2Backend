package com.group6.backend.user;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

// old implementation
@Entity
@Table(name = "users",
       indexes = {
         @Index(name = "idx_users_email", columnList = "email", unique = true),
         @Index(name = "idx_users_google_sub", columnList = "googleSub", unique = true)
       })
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(unique = true)
  private String googleSub;      // Google's subject (stable id)

  private String name;           // display name
  private String pictureUrl;     // avatar
  private Instant createdAt = Instant.now();

  public User() {}

  // Convenience ctor
  public User(String email, String name) {
    this.email = email;
    this.name = name;
  }

  // --- getters ---
  public Long getId() { return id; }
  public String getEmail() { return email; }
  public String getGoogleSub() { return googleSub; }
  public String getName() { return name; }
  public String getPictureUrl() { return pictureUrl; }
  public Instant getCreatedAt() { return createdAt; }

  // --- setters ---
  public void setEmail(String email) { this.email = email; }
  public void setGoogleSub(String googleSub) { this.googleSub = googleSub; }
  public void setName(String name) { this.name = name; }
  public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
