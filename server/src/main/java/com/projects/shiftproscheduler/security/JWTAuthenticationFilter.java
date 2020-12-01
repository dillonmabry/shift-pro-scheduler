package com.projects.shiftproscheduler.security;

import com.projects.shiftproscheduler.applicationuser.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static com.projects.shiftproscheduler.security.SecurityConstants.*;

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

        String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SIGNING_KEY));

        String token = Jwts.builder().setSubject(auth.getName()).claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(key).compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}