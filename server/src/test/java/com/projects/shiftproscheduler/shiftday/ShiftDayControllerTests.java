package com.projects.shiftproscheduler.shiftday;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class ShiftDayControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/shiftdays")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcShiftDaysAdmin() throws Exception {
    this.mockMvc.perform(get("/shiftdays")).andExpect(status().isOk());
  }

  @WithMockUser(username = "hleary", password = "test", roles = "USER")
  @Test
  void testMvcShiftDaysUser() throws Exception {
    this.mockMvc.perform(get("/shiftdays")).andExpect(status().isOk());
  }

}
