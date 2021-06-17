package com.projects.shiftproscheduler.administrator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.shiftproscheduler.security.JWTUtil;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class AdministratorControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AdministratorRepository administratorRepository;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/administrators")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "user", password = "user", roles = "USER")
  @Test
  void testMvcUnauthorizedUser() throws Exception {
    this.mockMvc.perform(get("/administrators")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAdministrators() throws Exception {
    this.mockMvc.perform(get("/administrators")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAdministrator() throws Exception {
    this.mockMvc.perform(get("/administrator/admin")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAdministratorSave() throws Exception {
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    Administrator admin = administratorRepository.findByUserName("admin").orElseThrow();
    admin.setEmail("newemail@test.com");

    this.mockMvc
        .perform(post("/administrators/admin").header("Authorization", "Bearer " + token)
            .content(new ObjectMapper().writeValueAsString(admin)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

}
