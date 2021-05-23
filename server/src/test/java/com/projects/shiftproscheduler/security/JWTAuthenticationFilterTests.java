package com.projects.shiftproscheduler.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class JWTAuthenticationFilterTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testAttemptAuthentication() throws Exception {
    this.mockMvc.perform(post("/login").content("{\"username\":\"admin\", \"password\":\"admin\"}")
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void testAttemptAuthenticationFailed() throws Exception {
    this.mockMvc.perform(post("/login").content("{\"username\":\"asdf\", \"password\":\"asdf\"}")
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
  }

}
