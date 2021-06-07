package com.projects.shiftproscheduler.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.projects.shiftproscheduler.applicationuser.ApplicationUser;
import com.projects.shiftproscheduler.applicationuser.ApplicationUserRepository;
import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.employee.EmployeeRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class ConfirmationTokenTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ConfirmationTokenRepository confirmationTokenRepository;

  @Autowired
  private ApplicationUserRepository applicationUserRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Test
  void testGenerateConfirmationToken() throws Exception {
    ApplicationUser user = applicationUserRepository.findByUsername("admin").orElseThrow();

    ConfirmationToken confirmationToken = new ConfirmationToken();
    confirmationToken.setUser(user);

    confirmationTokenRepository.save(confirmationToken);

    ConfirmationToken token = confirmationTokenRepository
        .findByConfirmationToken(confirmationToken.getConfirmationToken());
    assertNotNull(token);
    assertEquals(token.getConfirmationToken(), confirmationToken.getConfirmationToken());
    assertEquals("admin", confirmationToken.getUser().getUsername());
    assertNotNull(confirmationToken.getCreatedDate());
  }

  @Test
  void testConfirmationResponse() throws Exception {
    Employee employee = employeeRepository.findByUserName("hleary").orElseThrow();

    ApplicationUser user = new ApplicationUser();
    user.setUsername(employee.getUserName());
    user.setPassword("password");

    Set<Role> roles = new HashSet<Role>();
    roles.add(roleRepository.findByName("USER").orElseThrow());
    user.setRoles(roles);

    applicationUserRepository.save(user);

    ConfirmationToken token = new ConfirmationToken();
    token.setUser(user);
    ConfirmationToken confirmToken = confirmationTokenRepository.save(token);

    assertNotNull(confirmToken.getConfirmationToken());
    this.mockMvc.perform(get("/users/confirm-account/" + confirmToken.getConfirmationToken()))
        .andExpect(status().isOk());

  }

  @Test
  void testConfirmationResponseInvalidToken() throws Exception {
    this.mockMvc.perform(get("/users/confirm-account/" + UUID.randomUUID().toString()))
        .andExpect(status().is4xxClientError()).andExpect(jsonPath("$.message", is("Token invalid or link is broken")));
  }

}
