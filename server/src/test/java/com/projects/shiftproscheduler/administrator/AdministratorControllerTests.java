package com.projects.shiftproscheduler.administrator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projects.shiftproscheduler.assignment.Assignments;
import com.projects.shiftproscheduler.security.JWTUtil;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


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

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAdministrator() throws Exception {
    this.mockMvc.perform(get("/administrator/admin")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcPostSchedule() throws Exception {
    String startDate = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(7).toString();
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    MvcResult result = this.mockMvc
        .perform(
            post("/administrators/schedules/1/" + startDate + "/" + endDate).header("Authorization", "Bearer " + token)).andReturn();

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());
    Assignments assignments = mapper.readValue(result.getResponse().getContentAsString(), Assignments.class);
    assertNotNull(assignments.getAssignmentList());
    assertTrue(assignments.getAssignmentList().size() > 0);
  }

}
