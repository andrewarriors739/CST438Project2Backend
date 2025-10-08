package com.group6.backend.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/health", "/api/test-db", "/api/db-time").permitAll()  // allow health without auth
        .anyRequest().authenticated()                 // everything else protected (for later)
      )
      .httpBasic(basic -> {}); // keep simple Basic auth on protected routes for now
    return http.build();
  }
}
