package com.projects.shiftproscheduler.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;

import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.administrator.AdministratorRepository;
import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.department.DepartmentRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AdministratorRepository administrators;

  @Autowired
  private ScheduleRepository schedules;

  @Autowired
  private DepartmentRepository departments;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/schedule/1")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcSchedulesNotFound() throws Exception {
    Assertions.assertThrows(NestedServletException.class, () -> {
      this.mockMvc.perform(get("/schedule/5"));
    });
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcSchedules() throws Exception {

    Department department = new Department();
    department.setName("Supplies");
    departments.save(department);

    Administrator administrator = new Administrator();
    administrator.setFirstName("James");
    administrator.setLastName("Dean");
    administrator.setEmail("jdean@gmail.com");
    administrator.setUserName("jdean");
    administrator.setDepartment(departments.findByName("Supplies").orElseThrow());
    administrator.setPhone("000-000-0000");
    administrators.save(administrator);

    Schedule schedule = new Schedule();
    schedule.setAdministrator(administrator);
    schedule.setStartDate(LocalDate.now());
    schedule.setEndDate(LocalDate.now().plusDays(7));
    schedules.save(schedule);

    this.mockMvc.perform(get("/schedule/1")).andExpect(status().isOk());

    this.mockMvc.perform(post("/schedule/1")).andExpect(status().isOk());
    Schedule foundSchedule = schedules.findById(1).orElseThrow();
    assertTrue(foundSchedule.getIsActive());

    this.mockMvc.perform(delete("/schedule/1")).andExpect(status().isOk());
    Assertions.assertThrows(NestedServletException.class, () -> {
      this.mockMvc.perform(get("/schedule/1"));
    });
  }

}
