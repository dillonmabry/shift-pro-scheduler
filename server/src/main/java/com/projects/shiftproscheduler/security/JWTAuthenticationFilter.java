package com.projects.shiftproscheduler.security;

import com.projects.shiftproscheduler.applicationuser.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            ApplicationUser creds = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);

            return authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        String token = JWTUtil.generateToken(auth);
        String authorities = JWTUtil.getAuthorities(auth);

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(String.format("{\"accessToken\": \"%s\", \"username\": \"%s\", \"authorities\": \"%s\"}", token,
                auth.getName(), authorities));
        res.getWriter().flush();
    }
}