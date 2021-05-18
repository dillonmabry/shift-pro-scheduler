package com.projects.shiftproscheduler.department;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/departments")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcDepartmentsAdmin() throws Exception {
    this.mockMvc.perform(get("/departments")).andExpect(status().isOk());
  }

  @WithMockUser(username = "user", password = "user", roles = "USER")
  @Test
  void testMvcDepartmentsUser() throws Exception {
    this.mockMvc.perform(get("/departments")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcDepartmentsSave() throws Exception {
    Department department = new Department();
    department.setName("Retail");
    this.mockMvc.perform(post("/departments").content(new ObjectMapper().writeValueAsString(department))
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

}
