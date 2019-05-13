package com.projects.shiftproscheduler.employee;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class EmployeeController {

    private final EmployeeRepository employees;

    public EmployeeController(EmployeeRepository employeeService) {
        this.employees = employeeService;
    }

    @GetMapping({"/employees"})
    public @ResponseBody
    Employees getEmployees() {
        Employees employees = new Employees();
        employees.getEmployeeList().addAll(this.employees.findAll());
        return employees;
    }

}
