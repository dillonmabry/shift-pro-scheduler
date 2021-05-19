package com.projects.shiftproscheduler.employee;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.department.Department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/employees")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcEmployeesAdmin() throws Exception {
    this.mockMvc.perform(get("/employees")).andExpect(status().isOk());
  }

  @WithMockUser(username = "user", password = "user", roles = "USER")
  @Test
  void testMvcEmployeesUser() throws Exception {
    this.mockMvc.perform(get("/employees")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcEmployeesBySupervisor() throws Exception {
    this.mockMvc.perform(get("/employees/admin")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcEmployeesSave() throws Exception {
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

    this.mockMvc.perform(post("/employees").content(new ObjectMapper().writeValueAsString(employee))
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcEmployeesDelete() throws Exception {
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

    employee.setDepartment(department);
    employee.setSupervisor(admin);

    this.mockMvc.perform(post("/employees").content(new ObjectMapper().writeValueAsString(employee))
        .contentType(MediaType.APPLICATION_JSON));
    this.mockMvc.perform(delete("/employee/" + employee.getId())).andExpect(status().isOk());
  }

}
