package com.projects.shiftproscheduler.optimizer;

import java.util.Collection;

import com.projects.shiftproscheduler.employee.Employee;

public interface IOptimizer {

    Collection<Employee> getSchedule();
}
