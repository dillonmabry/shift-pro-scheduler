package com.projects.shiftproscheduler.shift;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import java.sql.Time;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class ShiftControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/shifts")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcShiftsAdmin() throws Exception {
    this.mockMvc.perform(get("/shifts")).andExpect(status().isOk());
  }

  @WithMockUser(username = "user", password = "user", roles = "USER")
  @Test
  void testMvcShiftsUser() throws Exception {
    this.mockMvc.perform(get("/shifts")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcShiftsSave() throws Exception {
    Shift shift = new Shift();
    shift.setStartTime(Time.valueOf("00:00:00"));
    shift.setEndTime(Time.valueOf("08:00:00"));
    this.mockMvc.perform(
        post("/shifts").content(new ObjectMapper().writeValueAsString(shift)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcShiftsDelete() throws Exception {
    Shift shift = new Shift();
    shift.setStartTime(Time.valueOf("00:00:00"));
    shift.setEndTime(Time.valueOf("08:00:00"));
    MvcResult result = this.mockMvc.perform(
        post("/shifts").content(new ObjectMapper().writeValueAsString(shift)).contentType(MediaType.APPLICATION_JSON))
        .andReturn();
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());
    Shift s = mapper.readValue(result.getResponse().getContentAsString(), Shift.class);
    this.mockMvc.perform(delete("/shift/" + s.getId())).andExpect(status().isOk());
  }

  @Test
  void testShiftString() {
    Shift shift = new Shift();
    shift.setId(1);
    shift.setStartTime(Time.valueOf("00:00:00"));
    shift.setEndTime(Time.valueOf("08:00:00"));
    assertEquals("Start Time: 00:00:00, End Time: 08:00:00", shift.toString());
  }

}
