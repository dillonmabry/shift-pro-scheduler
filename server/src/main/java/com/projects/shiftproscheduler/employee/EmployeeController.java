package com.projects.shiftproscheduler.employee;

import javax.persistence.EntityNotFoundException;

import com.projects.shiftproscheduler.administrator.AdministratorRepository;
import com.projects.shiftproscheduler.applicationuser.ApplicationUserRepository;
import com.projects.shiftproscheduler.department.DepartmentRepository;
import com.projects.shiftproscheduler.security.ConfirmationTokenRepository;
import com.projects.shiftproscheduler.security.JWTUtil;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {

    private final EmployeeRepository employees;
    private final AdministratorRepository administrators;
    private final ApplicationUserRepository applicationUsers;
    private final DepartmentRepository departments;
    private final ConfirmationTokenRepository confirmationTokens;

    public EmployeeController(EmployeeRepository employees, AdministratorRepository administrators,
            ApplicationUserRepository applicationUsers, DepartmentRepository departments,
            ConfirmationTokenRepository confirmationTokens) {
        this.employees = employees;
        this.administrators = administrators;
        this.applicationUsers = applicationUsers;
        this.departments = departments;
        this.confirmationTokens = confirmationTokens;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public @ResponseBody Employees getEmployees() {
        Employees employees = new Employees();
        employees.getEmployeeList().addAll(this.employees.findAll());
        return employees;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/employees/{supervisor}", method = RequestMethod.GET)
    public @ResponseBody Employees getEmployeesBySupervisor(
            @PathVariable(value = "supervisor", required = true) String supervisor) {
        Employees employees = new Employees();
        employees.getEmployeeList()
                .addAll(this.employees.findBySupervisor(administrators.findByUserName(supervisor).orElseThrow()));
        return employees;
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/employee/{username}", method = RequestMethod.GET)
    public @ResponseBody Employee getEmployee(@PathVariable(value = "username", required = true) String username,
            @RequestHeader("Authorization") String token) {
        UsernamePasswordAuthenticationToken authToken = JWTUtil.parseToken(token.split(" ")[1]);
        if (authToken == null || !authToken.getName().equals(username))
            throw new AccessDeniedException("Authenticated user does not have access to view employee details");
        return this.employees.findByUserName(username).orElseThrow(() -> new EntityNotFoundException());
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/employees/{username}")
    Employee saveEmployee(@RequestBody Employee newEmployee, @RequestHeader("Authorization") String token) {
        employees.findById(newEmployee.getId()).orElseThrow();
        UsernamePasswordAuthenticationToken authToken = JWTUtil.parseToken(token.split(" ")[1]);
        if (authToken == null || !authToken.getName().equals(newEmployee.getUserName()))
            throw new AccessDeniedException("Authenticated user does not have access to save employee details");
        return employees.save(newEmployee);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/employees")
    Employee saveEmployee(@RequestBody Employee newEmployee) {
        // Set default supervisor
        if (newEmployee.getSupervisor() == null)
            newEmployee.setSupervisor(administrators.findAll().iterator().next());
        // Set default department
        if (newEmployee.getDepartment() == null)
            newEmployee.setDepartment(departments.findAll().iterator().next());

        return employees.save(newEmployee);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/employee/{id}")
    void deleteEmployee(@PathVariable Integer id) {
        Employee employee = employees.findById(id).orElseThrow();
        applicationUsers.findByUsername(employee.getUserName()).ifPresentOrElse(user -> {
            confirmationTokens.deleteByUserId(user.getId());
            applicationUsers.delete(user);
            employees.deleteById(id);
        }, () -> {
            employees.deleteById(id);
        });
    }

}
