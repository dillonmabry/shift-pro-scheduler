package com.projects.shiftproscheduler.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import java.sql.Time;

import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.schedule.Schedule;
import com.projects.shiftproscheduler.shift.Shift;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AssignmentControllerTests {

  @Autowired
  private MockMvc mockMvc;

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

    assertEquals(assignment.getId(), 1);
    assertEquals(assignment.isNew(), false);
    assertEquals(assignment.getEmployee().getFirstName(), employee.getFirstName());
    assertEquals(assignment.getShift().getStartTime(), shift.getStartTime());
    assertEquals(assignment.getSchedule().getIsActive(), false);

    schedule.setIsActive(true);
    assertEquals(assignment.getSchedule().getIsActive(), true);
  }

}
