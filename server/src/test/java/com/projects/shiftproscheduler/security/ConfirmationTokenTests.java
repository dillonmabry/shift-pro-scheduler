package com.projects.shiftproscheduler.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.projects.shiftproscheduler.applicationuser.ApplicationUser;
import com.projects.shiftproscheduler.applicationuser.ApplicationUserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class ConfirmationTokenTests {

  @Autowired
  private ConfirmationTokenRepository confirmationTokenRepository;

  @Autowired
  private ApplicationUserRepository applicationUserRepository;

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

}
