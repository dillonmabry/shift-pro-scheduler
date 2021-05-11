package com.projects.shiftproscheduler.email;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private JavaMailSender javaMailSender;

  @Autowired
  public EmailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  @Async
  public void sendMail(SimpleMailMessage email) {
    javaMailSender.send(email);
  }

  @Async
  public void sendMail(MimeMessage email) {
    javaMailSender.send(email);
  }

  public MimeMessage createMimeMessage() {
    return javaMailSender.createMimeMessage();
  }

  public MimeMessageHelper createMimeMessageHelper(MimeMessage message) throws MessagingException {
    return new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
        StandardCharsets.UTF_8.name());
  }
}