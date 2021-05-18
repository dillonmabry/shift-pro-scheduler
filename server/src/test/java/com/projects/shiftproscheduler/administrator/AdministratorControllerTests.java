package com.projects.shiftproscheduler.administrator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AdministratorControllerTests {

  @Autowired
  private MockMvc mockMvc;

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

}
