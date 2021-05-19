package com.projects.shiftproscheduler.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTUtilTests {

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void testGenerateToken() throws Exception {
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    UsernamePasswordAuthenticationToken t = JWTUtil.parseToken(token);
    assertTrue(JWTUtil.getAuthorities(t).contains("ADMIN"));

    JWT jwt = new JWT();
    jwt.setAccessToken(token);
    jwt.setAuthorities(JWTUtil.getAuthorities(t));
    jwt.setUserName(t.getName());

    assertEquals(jwt.getUserName(), "admin");
    assertEquals(jwt.getAccessToken(), token);
    assertEquals(jwt.getAuthorities(), JWTUtil.getAuthorities(t));

  }

}
