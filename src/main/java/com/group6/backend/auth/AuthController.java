/*
package com.group6.backend.auth;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.group6.backend.user.User;
import com.group6.backend.user.UserRepository;

@Deprecated
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final GoogleIdTokenVerifier verifier;
  private final UserRepository users;
  private final JwtService jwt;

  public AuthController(
      @Value("${GOOGLE_CLIENT_ID}") String clientId,
      UserRepository users,
      JwtService jwt) {

    this.users = users;
    this.jwt = jwt;
    this.verifier = new GoogleIdTokenVerifier.Builder(
        new ApacheHttpTransport(),
        JacksonFactory.getDefaultInstance())
      .setAudience(Collections.singletonList(clientId))
      .build();
  }

  @PostMapping("/google")
  public ResponseEntity<?> google(@RequestBody Map<String, String> body) throws Exception {
    String idTokenString = body.get("idToken");
    if (idTokenString == null || idTokenString.isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error", "idToken is required"));
    }

    GoogleIdToken idToken = verifier.verify(idTokenString);
    if (idToken == null) {
      return ResponseEntity.status(401).body(Map.of("error", "Invalid Google ID token"));
    }

    GoogleIdToken.Payload p = idToken.getPayload();
    String sub = p.getSubject();
    String email = p.getEmail();
    String name = (String) p.get("name");
    String pic = (String) p.get("picture");

    User user = users.findByEmail(email).orElseGet(() -> {
      User u = new User();
      u.setGoogleSub(sub);
      u.setEmail(email);
      u.setName(name);
      u.setPictureUrl(pic);
      return users.save(u);
    });

    String token = jwt.createToken(user.getId(), user.getEmail());

    return ResponseEntity.ok(Map.of(
        "token", token,
        "user", Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "name", user.getName(),
            "picture", user.getPictureUrl()
        )
    ));
  }
}

*/