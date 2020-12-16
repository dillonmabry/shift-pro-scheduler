package com.projects.shiftproscheduler.applicationuser;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.security.ErrorInfo;
import com.projects.shiftproscheduler.security.Role;
import com.projects.shiftproscheduler.security.RoleRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class ApplicationUserController {

    private ApplicationUserRepository applicationUserRepository;
    private EmployeeRepository employeeRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApplicationUserController(ApplicationUserRepository applicationUserRepository,
            EmployeeRepository employeeRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.applicationUserRepository = applicationUserRepository;
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/register")
    public void register(@RequestBody ApplicationUser user) throws Exception {

        employeeRepository.findByUserName(user.getUsername()).orElseThrow();

        Set<Role> roles = new HashSet<Role>();
        roles.add(roleRepository.findByName("USER").orElseThrow());
        user.setRoles(roles);
        
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        applicationUserRepository.save(user);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody ErrorInfo
    duplicateUserNameException(HttpServletRequest req, DataIntegrityViolationException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex, "User already registered");
    }
}