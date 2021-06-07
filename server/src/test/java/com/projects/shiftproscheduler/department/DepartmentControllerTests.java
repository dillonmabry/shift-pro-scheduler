package com.projects.shiftproscheduler.department;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.employee.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testMvcUnauthorized() throws Exception {
    this.mockMvc.perform(get("/departments")).andExpect(status().is4xxClientError());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcDepartmentsAdmin() throws Exception {
    this.mockMvc.perform(get("/departments")).andExpect(status().isOk());
  }

  @WithMockUser(username = "user", password = "user", roles = "USER")
  @Test
  void testMvcDepartmentsUser() throws Exception {
    this.mockMvc.perform(get("/departments")).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcDepartmentsSave() throws Exception {
    Department department = new Department();
    department.setName("Electronics");

    MvcResult res = this.mockMvc.perform(post("/departments").content(new ObjectMapper().writeValueAsString(department))
        .contentType(MediaType.APPLICATION_JSON)).andReturn();
    Department dep = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(res.getResponse().getContentAsString(), Department.class);
    assertEquals("Electronics", dep.getName());

    this.mockMvc.perform(delete("/department/" + dep.getId())).andExpect(status().isOk());
  }

  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  @Test
  void testMvcDepartmentsDelete() throws Exception {
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

    this.mockMvc.perform(delete("/department/" + 1)).andExpect(status().is4xxClientError()).andExpect(jsonPath(
        "$.message",
        is("User(s) already exist with Department specified cannot take action. First remove Employee(s) with associated Department.")));
  };

}
