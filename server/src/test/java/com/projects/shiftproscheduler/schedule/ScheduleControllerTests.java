package com.projects.shiftproscheduler.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.employee.Employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ScheduleRepository schedules;

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

    Schedule s = schedules.save(schedule);

    this.mockMvc.perform(get("/schedule/" + s.getId())).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcScheduleActivate() throws Exception {
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

    Schedule s = schedules.save(schedule);
    assertEquals(s.getIsActive(), false);

    this.mockMvc.perform(post("/schedule/" + s.getId())).andExpect(status().isOk());
    Schedule foundSchedule = schedules.findById(s.getId()).orElseThrow();
    assertEquals(foundSchedule.getIsActive(), true);
  }
}
