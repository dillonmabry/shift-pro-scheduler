package com.projects.shiftproscheduler.employee;

import java.util.ArrayList;
import java.util.List;

public class Employees {

    private List<Employee> employees;

    public List<Employee> getEmployeeList() {
        if (employees == null) {
            employees = new ArrayList<>();
        }
        return employees;
    }

}
