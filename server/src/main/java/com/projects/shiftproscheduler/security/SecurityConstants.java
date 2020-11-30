package com.projects.shiftproscheduler.security;

public class SecurityConstants {
    public static final String SECRET = "ShiftProScheduleGenSecret";
    public static final long EXPIRATION_TIME = 86400000; // 1 day
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/register";
}