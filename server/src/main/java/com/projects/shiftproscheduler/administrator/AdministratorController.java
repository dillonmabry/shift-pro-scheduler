package com.projects.shiftproscheduler.administrator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;

import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.employee.Employees;
import com.projects.shiftproscheduler.optimizer.WeeklyOptimizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class AdministratorController {

    private final AdministratorRepository administrators;

    @Autowired
    public WeeklyOptimizer weeklyOptimizer;

    public AdministratorController(AdministratorRepository administrators) {
        this.administrators = administrators;
    }

    @GetMapping("/administrators")
    public @ResponseBody Administrators getAdministrators() {
        Administrators administrators = new Administrators();
        administrators.getAdministratorList().addAll(this.administrators.findAll());
        return administrators;
    }

    @GetMapping("/administrators/schedule/{period}")
    public @ResponseBody Employees getEmployeesSchedule(
            @PathVariable(value = "period", required = true) String period) {
        Collection<Employee> scheduledEmployees = new ArrayList<Employee>();

        switch (period) {
            case "weekly":
                scheduledEmployees = weeklyOptimizer.getSchedule();
                break;
            default:
                throw new InvalidParameterException();
        }

        Employees employees = new Employees();
        employees.getEmployeeList().addAll(scheduledEmployees);
        return employees;
    }

}
