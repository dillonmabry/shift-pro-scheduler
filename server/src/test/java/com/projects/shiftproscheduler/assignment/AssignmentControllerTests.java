package com.projects.shiftproscheduler.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.time.LocalDate;

import java.sql.Time;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.administrator.AdministratorRepository;
import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.schedule.Schedule;
import com.projects.shiftproscheduler.schedule.ScheduleRepository;
import com.projects.shiftproscheduler.shift.Shift;
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
public class AssignmentControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AdministratorRepository administratorRepository;

  @Autowired
  private ShiftRepository shiftRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/assignments")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAssignments() throws Exception {
    this.mockMvc.perform(get("/assignments")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAssignmentsSave() throws Exception {
    Assignment assignment = new Assignment();

    Schedule schedule = new Schedule();
    schedule.setAdministrator(administratorRepository.findByUserName("admin").orElseThrow());
    schedule.setStartDate(LocalDate.now());
    schedule.setEndDate(LocalDate.now().plusDays(7));
    Schedule s1 = scheduleRepository.save(schedule);

    assignment.setDayId(1);
    assignment.setEmployee(employeeRepository.findByUserName("jsmith").orElseThrow());
    assignment.setShift(shiftRepository.findById(1).orElseThrow());
    assignment.setSchedule(s1);

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    this.mockMvc
        .perform(
            post("/assignments").content(mapper.writeValueAsString(assignment)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAssignmentsDelete() throws Exception {
    Assignment assignment = new Assignment();

    Schedule schedule = new Schedule();
    schedule.setAdministrator(administratorRepository.findByUserName("admin").orElseThrow());
    schedule.setStartDate(LocalDate.now());
    schedule.setEndDate(LocalDate.now().plusDays(7));
    Schedule s1 = scheduleRepository.save(schedule);

    assignment.setDayId(2);
    assignment.setEmployee(employeeRepository.findByUserName("jsmith").orElseThrow());
    assignment.setShift(shiftRepository.findById(1).orElseThrow());
    assignment.setSchedule(s1);

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());

    MvcResult res = this.mockMvc
        .perform(
            post("/assignments").content(mapper.writeValueAsString(assignment)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn();

    Assignment newAssignment = mapper.readValue(res.getResponse().getContentAsString(), Assignment.class);
    assertEquals("jsmith", newAssignment.getEmployee().getUserName());

    this.mockMvc.perform(delete("/assignment/" + newAssignment.getId())).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcAssignmentProperties() throws Exception {
    Employee employee = new Employee();
    employee.setEmail("test@example.com");
    employee.setPhone("999-999-9999");
    employee.setUserName("testname");
    employee.setLastName("lastName");
    employee.setFirstName("firstName");
    employee.setId(1);

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

    Shift shift = new Shift();
    shift.setStartTime(Time.valueOf("00:00:00"));
    shift.setEndTime(Time.valueOf("08:00:00"));

    Assignment assignment = new Assignment();
    assignment.setEmployee(employee);
    assignment.setDayId(1);
    assignment.setSchedule(schedule);
    assignment.setShift(shift);
    assignment.setId(1);

    assertEquals(1, assignment.getId());
    assertEquals(false, assignment.isNew());
    assertEquals(assignment.getEmployee().getFirstName(), employee.getFirstName());
    assertEquals(assignment.getShift().getStartTime(), shift.getStartTime());
    assertEquals(false, assignment.getSchedule().getIsActive());

    schedule.setIsActive(true);
    assertEquals(true, assignment.getSchedule().getIsActive());
  }

}
