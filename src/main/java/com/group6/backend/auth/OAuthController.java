// src/main/java/com/group6/backend/auth/OAuthController.java
package com.group6.backend.auth;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.group6.backend.user.User;
import com.group6.backend.user.UserRepository;

@RestController
@RequestMapping("/auth/google")
public class OAuthController {

  private final String clientId;
  private final String clientSecret;
  private final String redirectUri;
  private final UserRepository users;
  private final JwtService jwt;
  private final AuthStore store;

  private final JacksonFactory json = JacksonFactory.getDefaultInstance();
  private final ApacheHttpTransport http = new ApacheHttpTransport();

  public OAuthController(
      @Value("${GOOGLE_CLIENT_ID}") String clientId,
      @Value("${GOOGLE_CLIENT_SECRET}") String clientSecret,
      @Value("${APP_BASE_URL}") String appBaseUrl,   //  https://group6-backend-...herokuapp.com
      UserRepository users,
      JwtService jwt,
      AuthStore store
  ) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = appBaseUrl + "/auth/google/callback";
    this.users = users;
    this.jwt = jwt;
    this.store = store;
  }

  private GoogleAuthorizationCodeFlow flow() throws Exception {
    return new GoogleAuthorizationCodeFlow.Builder(
        http,
        json,
        clientId,
        clientSecret,
        List.of("openid","email","profile"))
      .setAccessType("offline")
      .build();
  }

  @GetMapping("/start")
  public ResponseEntity<?> start(@RequestParam String deviceId) throws Exception {
    // put waiting state
    store.states.put(deviceId, new AuthWaitingState());

    String url = flow().newAuthorizationUrl()
        .setRedirectUri(redirectUri)
        .setState(deviceId)     
        .set("prompt", "consent")  
        .build();

    // return JSON with the URL; frontend opens it
    return ResponseEntity.ok(Map.of("url", url));
  }

  @GetMapping("/status")
public ResponseEntity<?> status(@RequestParam String deviceId) {
  AuthWaitingState s = store.states.get(deviceId);
  if (s == null) return ResponseEntity.ok(Map.of("status","NONE"));

  Map<String, Object> body = new java.util.HashMap<>();
  body.put("status", s.status.toString());
  if (s.error != null) body.put("error", s.error);
  if (s.jwt != null) body.put("jwt", s.jwt);
  if (s.user != null) body.put("user", s.user);

  return ResponseEntity.ok(body);
}


 @GetMapping("/callback")
public ResponseEntity<?> callback(
    @RequestParam String code,
    @RequestParam String state,   // deviceId
    @RequestParam(required=false,name="error") String oauthError
) throws Exception {

  AuthWaitingState s = store.states.get(state);
  if (s == null) {
    return ResponseEntity.status(400).body("unknown state");
  }

  if (oauthError != null) {
    s.status = AuthWaitingState.Status.ERROR;
    s.error = oauthError;
    // optional: redirect anywhere (or return a tiny "You can close this tab" page)
    return ResponseEntity.status(302).location(URI.create("https://accounts.google.com")).build();
  }

  try {
    GoogleTokenResponse token = flow()
        .newTokenRequest(code)
        .setRedirectUri(redirectUri)
        .execute();

    GoogleIdToken idToken = token.parseIdToken();
    GoogleIdToken.Payload p = idToken.getPayload();

    String sub   = p.getSubject();
    String email = p.getEmail();
    String name  = (String) p.get("name");
    String pic   = (String) p.get("picture");

    User user = users.findByEmail(email).orElseGet(() -> {
      User u = new User();
      u.setGoogleSub(sub);
      u.setEmail(email);
      u.setName(name);
      u.setPictureUrl(pic);
      return users.save(u);
    });

    String appJwt = jwt.createToken(user.getId(), user.getEmail());
    s.jwt = appJwt;
    s.user = Map.of(
        "id", user.getId(),
        "email", user.getEmail(),
        "name", user.getName(),
        "picture", user.getPictureUrl()
    );
    s.status = AuthWaitingState.Status.SUCCESS;

    // optional success redirect/page
    return ResponseEntity.status(302).location(URI.create("https://accounts.google.com")).build();

  } catch (com.google.api.client.auth.oauth2.TokenResponseException tre) {
    s.status = AuthWaitingState.Status.ERROR;
    String msg = tre.getDetails() != null
        ? tre.getDetails().getError() + ": " + tre.getDetails().getErrorDescription()
        : tre.getMessage();
    s.error = msg;
    // also log for your console
    tre.printStackTrace();
    return ResponseEntity.status(302).location(URI.create("https://accounts.google.com")).build();
  } catch (Exception e) {
    s.status = AuthWaitingState.Status.ERROR;
    s.error = e.getMessage();
    e.printStackTrace();
    return ResponseEntity.status(302).location(URI.create("https://accounts.google.com")).build();
  }
}

}
