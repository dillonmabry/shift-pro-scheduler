package com.projects.shiftproscheduler.employee;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.applicationuser.ApplicationUser;
import com.projects.shiftproscheduler.applicationuser.ApplicationUserRepository;
import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.security.JWT;
import com.projects.shiftproscheduler.security.JWTUtil;
import com.projects.shiftproscheduler.security.Role;
import com.projects.shiftproscheduler.security.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private ApplicationUserRepository applicationUserRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

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
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    this.mockMvc.perform(delete("/employee/" + employee.getId())).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "testname", roles = "USER")
  void testMvcEmployeeGetInvalid() throws Exception {
    this.mockMvc.perform(get("/employee/testname")).andExpect(status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "testname", roles = "USER")
  void testMvcEmployeeGet() throws Exception {
    String token = JWTUtil.generateToken(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(token);

    UsernamePasswordAuthenticationToken t = JWTUtil.parseToken(token);
    assertTrue(JWTUtil.getAuthorities(t).contains("USER"));

    JWT jwt = new JWT();
    jwt.setAccessToken(token);
    jwt.setAuthorities(JWTUtil.getAuthorities(t));
    jwt.setUserName(t.getName());

    assertNotNull(jwt);
    assertEquals("testname", jwt.getUserName());

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
    employeeRepository.save(employee);

    ApplicationUser user = new ApplicationUser();
    user.setUsername("testname");
    user.setIsActive(true);
    user.setPassword("password");
    Set<Role> roles = new HashSet<Role>();
    roles.add(roleRepository.findByName("USER").orElseThrow());
    user.setRoles(roles);

    applicationUserRepository.save(user);

    this.mockMvc.perform(get("/employee/" + employee.getUserName()).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());

    Employee newEmp = new Employee();
    newEmp.setEmail(employee.getEmail());
    newEmp.setPhone(employee.getPhone());
    newEmp.setUserName(employee.getUserName());
    newEmp.setLastName(employee.getLastName());
    newEmp.setFirstName("changedFirstName");
    newEmp.setId(employee.getId());
    newEmp.setDepartment(employee.getDepartment());
    newEmp.setSupervisor(employee.getSupervisor());
    this.mockMvc
        .perform(post("/employees/" + newEmp.getUserName()).header("Authorization", "Bearer " + token)
            .content(new ObjectMapper().writeValueAsString(newEmp)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

  }

}
