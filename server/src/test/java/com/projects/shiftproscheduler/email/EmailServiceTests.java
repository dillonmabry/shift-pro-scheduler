package com.projects.shiftproscheduler.email;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.MimeMessageHelper;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailServiceTests {

  @Autowired
  private EmailService emailService;

  @Test
  void testCreateMimeMessage() throws Exception {
    MimeMessage message = emailService.createMimeMessage();
    assertEquals(message.getSize(), -1);
  }

  @Test
  void testCreateMimeMessageHelper() throws Exception {
    MimeMessage message = emailService.createMimeMessage();
    MimeMessageHelper messageHelper = emailService.createMimeMessageHelper(message);
    assertEquals(messageHelper.getEncoding(), StandardCharsets.UTF_8.name());
    System.out.println(messageHelper.getMimeMultipart());
    assertEquals(messageHelper.isMultipart(), true);
  }

}