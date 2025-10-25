package com.group6.backend.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// old implementation
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByGoogleSub(String googleSub);
  Optional<User> findByEmail(String email);
}
