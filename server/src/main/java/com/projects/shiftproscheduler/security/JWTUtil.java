package com.projects.shiftproscheduler.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import static com.projects.shiftproscheduler.security.SecurityConstants.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

public class JWTUtil {

    public static UsernamePasswordAuthenticationToken parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SIGNING_KEY));
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token.replace(TOKEN_PREFIX, ""));

        Claims claims = claimsJws.getBody();
        String user = (String) claims.get("sub");

        if (user != null) {
            Collection<SimpleGrantedAuthority> authorities = Arrays
                    .stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        }
        return null;
    }

    public static String getAuthorities(Authentication auth) {
        String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return authorities;
    }

    public static String generateToken(Authentication auth) {
        String authorities = getAuthorities(auth);

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SIGNING_KEY));

        String token = Jwts.builder().setSubject(auth.getName()).claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(key).compact();
        return token;
    }
}
