package com.projects.shiftproscheduler.applicationuser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import com.projects.shiftproscheduler.email.EmailService;
import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.security.ConfirmationToken;
import com.projects.shiftproscheduler.security.ConfirmationTokenRepository;
import com.projects.shiftproscheduler.security.ErrorInfo;
import com.projects.shiftproscheduler.security.Role;
import com.projects.shiftproscheduler.security.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@RestController
@RequestMapping("/users")
public class ApplicationUserController {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("classpath:/static/images/handshake-icon.png")
    Resource welcomeResource;

    @PostMapping("/register")
    @Transactional
    public void register(@RequestBody ApplicationUser user) throws Exception {

        employeeRepository.findByUserName(user.getUsername()).orElseThrow();

        Set<Role> roles = new HashSet<Role>();
        roles.add(roleRepository.findByName("USER").orElseThrow());
        user.setRoles(roles);

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        applicationUserRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(user);
        confirmationTokenRepository.save(confirmationToken);

        Employee employee = employeeRepository.findByUserName(user.getUsername()).orElseThrow();

        MimeMessage message = emailService.createMimeMessage();
        MimeMessageHelper helper = emailService.createMimeMessageHelper(message);

        String adminEmail = "shiftproadmin@shiftproscheduler.com";
        Map<String, Object> mailProps = new HashMap<String, Object>() {
            {
                put("confirmLink",
                        "http://localhost:8080/api/users/confirm-account/" + confirmationToken.getConfirmationToken());
                put("adminMail", adminEmail);
            }
        };
        Context context = new Context();
        context.setVariables(mailProps);
        String html = templateEngine.process("email-confirmation", context);

        helper.setTo(employee.getEmail());
        helper.setText(html, true);
        helper.setSubject("Complete Shift Pro Registration!");
        helper.setFrom(adminEmail);

        helper.addInline("handshake-icon.png", welcomeResource);
        emailService.sendMail(message);
    }

    @GetMapping(value = "/confirm-account/{token}")
    @Transactional
    public ResponseEntity<String> confirmUserAccount(@PathVariable("token") String confirmationToken) throws Exception {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if (token == null)
            throw new IllegalArgumentException();

        ApplicationUser user = applicationUserRepository.findByUsername(token.getUser().getUsername()).get();
        user.setIsActive(true);
        applicationUserRepository.save(user);

        Context context = new Context();
        String html = templateEngine.process("email-successful", context);

        return new ResponseEntity<String>(html, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    ErrorInfo duplicateUserNameException(HttpServletRequest req, DataIntegrityViolationException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex, "User already registered");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    ErrorInfo invalidConfirmationTokenException(HttpServletRequest req, IllegalArgumentException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex, "Token invalid or link is broken");
    }
}