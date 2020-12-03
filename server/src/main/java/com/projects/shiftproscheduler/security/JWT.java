package com.projects.shiftproscheduler.security;

import java.util.List;

public class JWT {

    private String username;

    private List<String> authorities;

    private String accessToken;

    public JWT() {

    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getUserName() {
        return username;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
