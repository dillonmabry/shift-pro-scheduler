package com.projects.shiftproscheduler.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.assignmentrequest.AssignmentRequest;
import com.projects.shiftproscheduler.assignmentrequest.AssignmentRequestRepository;
import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.security.JWTUtil;
import com.projects.shiftproscheduler.shift.Shift;
import com.projects.shiftproscheduler.shift.ShiftRepository;
import com.projects.shiftproscheduler.shiftday.ShiftDayRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class ScheduleControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ScheduleRepository schedules;

  @Autowired
  private AssignmentRequestRepository assignmentRequestRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private ShiftRepository shiftRepository;

  @Autowired
  private ShiftDayRepository shiftDayRepository;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/schedule/1")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcSchedules() throws Exception {
    this.mockMvc.perform(get("/schedules")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcSchedule() throws Exception {
    Department department = new Department();
    department.setId(1);
    department.setName("Supplies");

    Administrator admin = new Administrator();
    admin.setDepartment(department);
    admin.setEmail("test@example.com");
    admin.setFirstName("admin");
    admin.setLastName("admin");
    admin.setPhone("999-999-9999");
    admin.setUserName("admin");
    admin.setId(1);

    Schedule schedule = new Schedule();
    schedule.setAdministrator(admin);
    schedule.setStartDate(LocalDate.now());
    schedule.setEndDate(LocalDate.now().plusDays(7));

    Schedule s = schedules.save(schedule);

    this.mockMvc.perform(get("/schedule/" + s.getId())).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcSchedulesDelete() throws Exception {
    Department d1 = new Department();
    d1.setId(1);
    d1.setName("Supplies");

    Administrator a1 = new Administrator();
    a1.setDepartment(d1);
    a1.setEmail("test@example.com");
    a1.setFirstName("admin");
    a1.setLastName("admin");
    a1.setPhone("999-999-9999");
    a1.setUserName("admin");
    a1.setId(1);

    Schedule s1 = new Schedule();
    s1.setAdministrator(a1);
    s1.setStartDate(LocalDate.now());
    s1.setEndDate(LocalDate.now().plusDays(7));

    Schedule savedS1 = schedules.save(s1);
    assertEquals(false, savedS1.getIsActive());

    this.mockMvc.perform(post("/schedule/" + savedS1.getId())).andExpect(status().isOk());

    this.mockMvc.perform(delete("/schedule/" + savedS1.getId())).andExpect(status().isOk());
  };

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcSchedulesActivateException() throws Exception {
    Department d1 = new Department();
    d1.setId(1);
    d1.setName("Supplies");

    Administrator a1 = new Administrator();
    a1.setDepartment(d1);
    a1.setEmail("test@example.com");
    a1.setFirstName("admin");
    a1.setLastName("admin");
    a1.setPhone("999-999-9999");
    a1.setUserName("admin");
    a1.setId(1);

    Schedule s1 = new Schedule();
    s1.setAdministrator(a1);
    s1.setStartDate(LocalDate.now());
    s1.setEndDate(LocalDate.now().plusDays(7));

    Schedule savedS1 = schedules.save(s1);
    assertEquals(false, savedS1.getIsActive());

    this.mockMvc.perform(post("/schedule/" + savedS1.getId())).andExpect(status().isOk());
    Schedule f1 = schedules.findById(savedS1.getId()).orElseThrow();
    assertEquals(true, f1.getIsActive());

    Department d2 = new Department();
    d2.setId(1);
    d2.setName("Supplies");

    Administrator a2 = new Administrator();
    a2.setDepartment(d2);
    a2.setEmail("test@example.com");
    a2.setFirstName("admin");
    a2.setLastName("admin");
    a2.setPhone("999-999-9999");
    a2.setUserName("admin");
    a2.setId(1);

    Schedule s2 = new Schedule();
    s2.setAdministrator(a2);
    s2.setStartDate(LocalDate.now().minusDays(2)); // within any period
    s2.setEndDate(LocalDate.now().plusDays(10)); // within any period

    Schedule savedS2 = schedules.save(s2);
    assertEquals(false, savedS2.getIsActive());

    this.mockMvc.perform(post("/schedule/" + savedS2.getId())).andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.message", is("Active schedule with date range selected already exists")));
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcPostScheduleDefault() throws Exception {
    String startDate = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(7).toString();
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    MvcResult result = this.mockMvc.perform(
        post("/schedules/DefaultOptimizer/1/" + startDate + "/" + endDate).header("Authorization", "Bearer " + token))
        .andReturn();

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    Schedules schedules = mapper.readValue(result.getResponse().getContentAsString(), Schedules.class);
    assertNotNull(schedules.getScheduleList());
    assertTrue(schedules.getScheduleList().size() > 0);
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcPostScheduleDefaultTooManyShifts() throws Exception {
    String startDate = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(7).toString();
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    String[] times = { "03:00:00", "04:00:00", "05:00:00", "06:00:00" };
    ArrayList<Shift> addedShifts = new ArrayList<Shift>();
    for (String time : times) {
      Shift shift = new Shift();
      shift.setStartTime(Time.valueOf("00:00:00"));
      shift.setEndTime(Time.valueOf(time));
      MvcResult result = this.mockMvc.perform(
          post("/shifts").content(new ObjectMapper().writeValueAsString(shift)).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk()).andReturn();
      ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      mapper.registerModule(new JavaTimeModule());
      Shift s = mapper.readValue(result.getResponse().getContentAsString(), Shift.class);
      addedShifts.add(s);
    }

    Exception exception = assertThrows(NestedServletException.class, () -> {
      this.mockMvc.perform(
          post("/schedules/DefaultOptimizer/1/" + startDate + "/" + endDate).header("Authorization", "Bearer " + token))
          .andReturn();
    });

    assertEquals(
        "Request processing failed; nested exception is java.lang.IllegalStateException: Not enough employees for shifts required",
        exception.getMessage());
    
    addedShifts.forEach(shift -> {
      try {
        this.mockMvc.perform(delete("/shift/" + shift.getId())).andExpect(status().isOk());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcPostSchedulePreferenceTooManyShifts() throws Exception {
    String startDate = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(7).toString();
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    String[] times = { "03:00:00", "04:00:00", "05:00:00", "06:00:00" };
    ArrayList<Shift> addedShifts = new ArrayList<Shift>();
    for (String time : times) {
      Shift shift = new Shift();
      shift.setStartTime(Time.valueOf("00:00:00"));
      shift.setEndTime(Time.valueOf(time));
      MvcResult result = this.mockMvc.perform(
          post("/shifts").content(new ObjectMapper().writeValueAsString(shift)).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk()).andReturn();
      ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      mapper.registerModule(new JavaTimeModule());
      Shift s = mapper.readValue(result.getResponse().getContentAsString(), Shift.class);
      addedShifts.add(s);
    }

    Exception exception = assertThrows(NestedServletException.class, () -> {
      this.mockMvc.perform(
          post("/schedules/PreferenceOptimizer/1/" + startDate + "/" + endDate).header("Authorization", "Bearer " + token))
          .andReturn();
    });

    assertEquals(
        "Request processing failed; nested exception is java.lang.IllegalStateException: Not enough employees for shifts required",
        exception.getMessage());
    
    addedShifts.forEach(shift -> {
      try {
        this.mockMvc.perform(delete("/shift/" + shift.getId())).andExpect(status().isOk());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcPostSchedulePreference() throws Exception {
    // Setup assignment requests
    AssignmentRequest assignmentRequest = new AssignmentRequest();

    assignmentRequest.setShift(shiftRepository.findById(2).orElseThrow()); // '16:00:00', '23:59:59'
    assignmentRequest.setEmployee(employeeRepository.findByUserName("athomas").orElseThrow()); // athomas
    assignmentRequest.setShiftDay(shiftDayRepository.findById(4).orElseThrow()); // Thursday
    assignmentRequestRepository.save(assignmentRequest);

    // Post the schedule optimizer
    String startDate = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(7).toString();
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    MvcResult result = this.mockMvc.perform(post("/schedules/PreferenceOptimizer/1/" + startDate + "/" + endDate)
        .header("Authorization", "Bearer " + token)).andReturn();

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    Schedules schedules = mapper.readValue(result.getResponse().getContentAsString(), Schedules.class);
    assertNotNull(schedules.getScheduleList());
    assertTrue(schedules.getScheduleList().size() > 0);
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test()
  void testMvcPostScheduleOptimizerNotFound() throws Exception {
    String startDate = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(7).toString();
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    Exception exception = assertThrows(NestedServletException.class, () -> {
      this.mockMvc.perform(
          post("/schedules/asdfasdf/1/" + startDate + "/" + endDate).header("Authorization", "Bearer " + token));
    });

    assertEquals(
        "Request processing failed; nested exception is java.lang.IllegalArgumentException: No optimizer available for input provided",
        exception.getMessage());
  }
}
