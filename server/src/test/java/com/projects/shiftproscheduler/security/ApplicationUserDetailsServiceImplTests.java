package com.projects.shiftproscheduler.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;

import com.projects.shiftproscheduler.applicationuser.ApplicationUser;
import com.projects.shiftproscheduler.applicationuser.ApplicationUserDetailsServiceImpl;
import com.projects.shiftproscheduler.applicationuser.ApplicationUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationUserDetailsServiceImplTests {

  @Autowired
  private ApplicationUserRepository applicationUsers;

  @Autowired
  private ApplicationUserDetailsServiceImpl service;

  @Autowired
  private RoleRepository roleRepository;

  @Test
  void testGetAuthorities() throws Exception {

    ApplicationUser user = new ApplicationUser();
    user.setUsername("testuser");
    user.setIsActive(true);
    user.setPassword("password");
    Set<Role> roles = new HashSet<Role>();
    Role testRole = roleRepository.findByName("ADMIN").orElseThrow();

    testRole.setName("testName");
    assertEquals("testName", testRole.getName());

    testRole.setName("ADMIN");
    assertEquals("ADMIN", testRole.getName());
    assertEquals("application administrator", testRole.getDescription());

    testRole.setDescription("new desc");
    assertEquals("new desc", testRole.getDescription());

    roles.add(testRole);
    user.setRoles(roles);

    applicationUsers.save(user);

    UserDetails userDetails = service.loadUserByUsername("testuser");
    assertEquals("testuser", userDetails.getUsername());
    assertEquals(1, userDetails.getAuthorities().size());
  }

  @Test
  void testUserAccountLock() throws Exception {

    ApplicationUser user = new ApplicationUser();
    user.setUsername("testuser2");
    user.setIsActive(false);
    user.setPassword("password");
    Set<Role> roles = new HashSet<Role>();
    Role testRole = roleRepository.findByName("USER").orElseThrow();

    testRole.setName("USER");
    assertEquals("USER", testRole.getName());
    assertEquals("application user", testRole.getDescription());

    roles.add(testRole);
    user.setRoles(roles);

    applicationUsers.save(user);

    LockedException exception = assertThrows(LockedException.class, () -> {
      service.loadUserByUsername("testuser2");
    });
    assertEquals("User account not activated", exception.getMessage());
  }

}
