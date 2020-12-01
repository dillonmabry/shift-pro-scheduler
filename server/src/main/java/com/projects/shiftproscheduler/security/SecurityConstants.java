package com.projects.shiftproscheduler.security;

public class SecurityConstants {
    public static final String SIGNING_KEY = "D4MB2kPVwJ4VeTvlupY25UQMs9/NaA4yiy7/ELvuXpg=";
    public static final long EXPIRATION_TIME = 86400000; // 1 day
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/register";
    public static final String AUTHORITIES_KEY = "scopes";
}