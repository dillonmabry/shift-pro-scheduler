package com.projects.shiftproscheduler.employee;

import javax.persistence.EntityNotFoundException;

import com.projects.shiftproscheduler.security.JWTUtil;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {

    private final EmployeeRepository employees;

    public EmployeeController(EmployeeRepository employeeService) {
        this.employees = employeeService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public @ResponseBody Employees getEmployees() {
        Employees employees = new Employees();
        employees.getEmployeeList().addAll(this.employees.findAll());
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

}
