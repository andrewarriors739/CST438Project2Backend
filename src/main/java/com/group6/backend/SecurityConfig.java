package com.group6.backend;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(cors -> cors.configurationSource(req -> {
        CorsConfiguration c = new CorsConfiguration();
        // add your frontend origins here as needed
        c.setAllowedOrigins(List.of("*"));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        return c;
      }))
      .authorizeHttpRequests(auth -> auth
        // public: landing + static + health/dev endpoints
        .requestMatchers(
          "/", "/index.html", "/error", "/favicon.ico",
          "/static/**", "/assets/**", "/css/**", "/js/**",
          "/actuator/**",
          "/api/health", "/api/test-db", "/api/db-time",
          "/api/words", "/api/words/**"
        ).permitAll()
        // preflight
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        // everything else is also public for now (no login prompt)
        .anyRequest().permitAll()
      );
      // ⚠️ No httpBasic() while we’re public; that’s what triggers the browser prompt

    return http.build();
  }
}

