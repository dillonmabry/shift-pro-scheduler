package com.projects.shiftproscheduler.assignmentrequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.security.JWTUtil;
import com.projects.shiftproscheduler.shift.ShiftRepository;
import com.projects.shiftproscheduler.shiftday.ShiftDayRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

  @Autowired
  private ShiftDayRepository shiftDayRepository;

  @Autowired
  private AssignmentRequestRepository assignmentRequestRepository;

  @Test
  void testMvcUnauthorizedRequests() throws Exception {
    this.mockMvc.perform(get("/assignmentrequests")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAssignmentRequests() throws Exception {
    this.mockMvc.perform(get("/assignmentrequests")).andExpect(status().isOk());
  }

  @WithMockUser(username = "hleary", password = "test", roles = "USER")
  @Test
  void testMvcAssignmentRequestsByEmployee() throws Exception {
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    UsernamePasswordAuthenticationToken t = JWTUtil.parseToken(token);

    this.mockMvc.perform(get("/assignmentrequests/" + t.getName()).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
  }

  @WithMockUser(username = "athomas", password = "test", roles = "USER")
  @Test
  void testMvcAssignmentRequestsSave() throws Exception {
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());

    AssignmentRequest assignmentRequest = new AssignmentRequest();

    assignmentRequest.setShift(shiftRepository.findById(1).orElseThrow());
    assignmentRequest.setEmployee(employeeRepository.findByUserName("athomas").orElseThrow());
    assignmentRequest.setShiftDay(shiftDayRepository.findById(1).orElseThrow());

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    this.mockMvc
        .perform(post("/assignmentrequests").content(mapper.writeValueAsString(assignmentRequest))
            .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @WithMockUser(username = "hleary", password = "test", roles = "USER")
  @Test
  void testMvcAssignmentRequestsSaveNullFields() throws Exception {
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());

    AssignmentRequest assignmentRequest = new AssignmentRequest();

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    this.mockMvc
        .perform(post("/assignmentrequests").content(mapper.writeValueAsString(assignmentRequest))
            .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @WithMockUser(username = "jsmith", password = "test", roles = "USER")
  @Test
  void testMvcAssignmentRequestsDelete() throws Exception {
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());

    AssignmentRequest assignmentRequest = new AssignmentRequest();

    assignmentRequest.setShift(shiftRepository.findById(1).orElseThrow());
    assignmentRequest.setEmployee(employeeRepository.findByUserName("jsmith").orElseThrow());
    assignmentRequest.setShiftDay(shiftDayRepository.findById(1).orElseThrow());

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    MvcResult res = this.mockMvc
        .perform(post("/assignmentrequests").content(mapper.writeValueAsString(assignmentRequest))
            .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    AssignmentRequest request = mapper.readValue(res.getResponse().getContentAsString(), AssignmentRequest.class);

    this.mockMvc.perform(delete("/assignmentrequest/" + request.getId()).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
  }

  @WithMockUser(username = "athomas", password = "test", roles = "USER")
  @Test
  void testMvcAssignmentRequestsFindBy() throws Exception {
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());

    AssignmentRequest assignmentRequest = new AssignmentRequest();

    assignmentRequest.setShift(shiftRepository.findById(2).orElseThrow());
    assignmentRequest.setEmployee(employeeRepository.findByUserName("athomas").orElseThrow());
    assignmentRequest.setShiftDay(shiftDayRepository.findById(4).orElseThrow());

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    this.mockMvc
        .perform(post("/assignmentrequests").content(mapper.writeValueAsString(assignmentRequest))
            .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    Collection<AssignmentRequest> requests = assignmentRequestRepository.findAll();

    assertTrue(requests.size() > 0);
  }

}
