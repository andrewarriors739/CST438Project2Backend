package com.group6.backend.auth;

import java.util.Map;

public class AuthWaitingState {
  public enum Status { WAITING, SUCCESS, ERROR }
  public Status status = Status.WAITING;
  public String error;
  public String jwt;
  public Map<String,Object> user;
}