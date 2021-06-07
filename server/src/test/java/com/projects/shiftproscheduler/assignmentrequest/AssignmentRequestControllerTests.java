package com.projects.shiftproscheduler.assignmentrequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.shift.ShiftRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class AssignmentRequestControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ShiftRepository shiftRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/assignmentrequests")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAssignmentRequests() throws Exception {
    this.mockMvc.perform(get("/assignmentrequests")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAssignmentRequestsSave() throws Exception {
    AssignmentRequest assignmentRequest = new AssignmentRequest();

    assignmentRequest.setShift(shiftRepository.findById(1).orElseThrow());
    assignmentRequest.setEmployee(employeeRepository.findByUserName("hleary").orElseThrow());
    assignmentRequest.setRequestDate(LocalDate.now());

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    this.mockMvc.perform(post("/assignmentrequests").content(mapper.writeValueAsString(assignmentRequest))
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAssignmentRequestsDelete() throws Exception {

    AssignmentRequest assignmentRequest = new AssignmentRequest();

    assignmentRequest.setShift(shiftRepository.findById(1).orElseThrow());
    assignmentRequest.setEmployee(employeeRepository.findByUserName("hleary").orElseThrow());
    assignmentRequest.setRequestDate(LocalDate.now());

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    MvcResult res = this.mockMvc.perform(post("/assignmentrequests")
        .content(mapper.writeValueAsString(assignmentRequest)).contentType(MediaType.APPLICATION_JSON)).andReturn();

    AssignmentRequest request = mapper.readValue(res.getResponse().getContentAsString(), AssignmentRequest.class);

    this.mockMvc.perform(delete("/assignmentrequest/" + request.getId())).andExpect(status().isOk());
  }

}
