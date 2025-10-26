package com.group6.backend;

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
      .requestMatchers("/h2-console/**").permitAll()
        .requestMatchers("/api/health", "/api/test-db", "/api/db-time", "/api/words").permitAll()  // allow health without auth
        .anyRequest().authenticated()                 // everything else protected (for later)
      )
      .httpBasic(basic -> {}); // keep simple Basic auth on protected routes for now
    return http.build();
  }
}
