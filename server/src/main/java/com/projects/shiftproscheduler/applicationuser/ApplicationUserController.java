package com.projects.shiftproscheduler.applicationuser;

import java.util.HashSet;
import java.util.Set;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
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

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        Employee employee = employeeRepository.findByUserName(user.getUsername()).orElseThrow();
        mailMessage.setTo(employee.getEmail());
        mailMessage.setSubject("Complete Shift Pro Registration!");
        mailMessage.setFrom("shiftproadmin@shiftproscheduler.com");
        mailMessage.setText("To confirm your Shift Pro account, please click here : "
                + "http://localhost:8080/api/users/confirm-account/" + confirmationToken.getConfirmationToken());

        emailService.sendMail(mailMessage);
    }

    @GetMapping(value = "/confirm-account/{token}")
    public ResponseEntity<String> confirmUserAccount(@PathVariable("token") String confirmationToken) throws Exception {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if (token == null)
            throw new Exception("Token invalid or link is broken");

        ApplicationUser user = applicationUserRepository.findByUsername(token.getUser().getUsername()).get();
        user.setIsActive(true);
        applicationUserRepository.save(user);

        return new ResponseEntity<String>("Successful confirmation", HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    ErrorInfo duplicateUserNameException(HttpServletRequest req, DataIntegrityViolationException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex, "User already registered");
    }
}