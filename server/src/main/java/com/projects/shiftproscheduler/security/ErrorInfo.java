package com.projects.shiftproscheduler.security;

public class ErrorInfo {
    public final String url;
    public final String ex;
    public final String message;

    public ErrorInfo(String url, Exception ex, String msg) {
        this.url = url;
        this.ex = ex.getLocalizedMessage();
        this.message = msg;
    }
}