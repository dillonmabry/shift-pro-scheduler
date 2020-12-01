package com.projects.shiftproscheduler.employee;

import org.springframework.security.access.prepost.PreAuthorize;
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

}
