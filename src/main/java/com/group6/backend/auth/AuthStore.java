// src/main/java/com/group6/backend/auth/AuthStore.java
package com.group6.backend.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class AuthStore {
  public final Map<String, AuthWaitingState> states = new ConcurrentHashMap<>();
}
