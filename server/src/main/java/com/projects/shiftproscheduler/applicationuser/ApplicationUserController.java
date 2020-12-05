package com.projects.shiftproscheduler.applicationuser;

import java.util.HashSet;
import java.util.Set;

import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.security.Role;
import com.projects.shiftproscheduler.security.RoleRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}