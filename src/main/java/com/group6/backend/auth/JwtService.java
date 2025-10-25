// src/main/java/com/group6/backend/auth/JwtService.java

package com.group6.backend.auth;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class JwtService {

  private final Algorithm alg;
  private final String issuer;

  public JwtService(
      @Value("${JWT_SECRET}") String secret,
      @Value("${APP_BASE_URL:http://localhost:8080}") String issuer) {
    this.alg = Algorithm.HMAC256(secret);
    this.issuer = issuer;
  }

  public String createToken(Long userId, String email) {
    Instant now = Instant.now();
    return JWT.create()
        .withIssuer(issuer)
        .withIssuedAt(now)
        .withExpiresAt(now.plusSeconds(60 * 60 * 24 * 7)) // 7 days
        .withClaim("uid", userId)
        .withClaim("email", email)
        .sign(alg);
  }
}
