package com.group6.backend;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
        // DEV: allow all. PROD: replace with your real app origins (e.g., https://your-expo.dev, https://*.herokuapp.com)
        c.setAllowedOriginPatterns(List.of("*"));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setExposedHeaders(List.of("Authorization")); // optional but handy
        c.setAllowCredentials(true);
        c.setMaxAge(3600L);
        return c;
      }))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/", "/index.html", "/error", "/favicon.ico",
          "/static/**", "/assets/**", "/css/**", "/js/**",
          "/actuator/**",
          "/api/health", "/api/test-db", "/api/db-time",
          "/api/words", "/api/words/**",
          // OAuth flow endpoints must be public
          "/auth/**"
        ).permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        // Phase A: leave public; Phase B: change to .anyRequest().authenticated()
        .anyRequest().permitAll()
      )
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // No httpBasic(), no formLogin()
    return http.build();
  }
}

