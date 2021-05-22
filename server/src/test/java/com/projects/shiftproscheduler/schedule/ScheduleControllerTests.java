package com.projects.shiftproscheduler.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.department.Department;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;

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
    assertEquals(savedS1.getIsActive(), false);

    this.mockMvc.perform(post("/schedule/" + savedS1.getId())).andExpect(status().isOk());

    this.mockMvc.perform(delete("/schedule/" + savedS1.getId())).andExpect(status().isOk());
  };

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcSchedulesActivate() throws Exception {
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
    assertEquals(savedS1.getIsActive(), false);

    this.mockMvc.perform(post("/schedule/" + savedS1.getId())).andExpect(status().isOk());
    Schedule f1 = schedules.findById(savedS1.getId()).orElseThrow();
    assertEquals(f1.getIsActive(), true);
  }

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
    assertEquals(savedS1.getIsActive(), false);

    this.mockMvc.perform(post("/schedule/" + savedS1.getId())).andExpect(status().isOk());
    Schedule f1 = schedules.findById(savedS1.getId()).orElseThrow();
    assertEquals(f1.getIsActive(), true);

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
    assertEquals(savedS2.getIsActive(), false);

    this.mockMvc.perform(post("/schedule/" + savedS2.getId())).andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.message", is("Active schedule with date range selected already exists")));
  }
}
