package com.projects.shiftproscheduler.optimizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.schedule.Schedules;
import com.projects.shiftproscheduler.shift.Shift;
import com.projects.shiftproscheduler.shift.ShiftRepository;

class DefaultSolutionGenerator extends CpSolverSolutionCallback {

  private Schedules assignedSchedules;

  private Collection<Assignment> scheduledAssignments = new ArrayList<Assignment>();

  private final EmployeeRepository employeeRepository;

  private final ShiftRepository shiftRepository;

  private int solutionCount = 0;
  private final IntVar[] variableArray;
  private final int solutionLimit;

  public DefaultSolutionGenerator(Schedules schedules, EmployeeRepository employeeRepository,
      ShiftRepository shiftRepository, IntVar[] variables) {
    this.assignedSchedules = schedules;
    this.employeeRepository = employeeRepository;
    this.shiftRepository = shiftRepository;
    this.variableArray = variables;
    this.solutionLimit = schedules.getScheduleList().size();
  }

  @Override
  public void onSolutionCallback() {

    if (solutionCount < this.solutionLimit) {
      for (IntVar v : this.variableArray) {
        String[] varValues = v.getName().split("_"); // Get constraint var values
        if (this.value(v) == 1) {
          // Get assignment values
          Optional<Employee> employeeUser = employeeRepository.findById(Integer.parseInt(varValues[1]));
          Employee emp = employeeUser.orElseThrow();
          int dayId = Integer.parseInt(varValues[2]);
          Shift shift = shiftRepository.findById(Integer.parseInt(varValues[3])).orElseThrow();
          // Set assignment request
          Assignment assignment = new Assignment();
          assignment.setEmployee(emp);
          assignment.setDayId(dayId);
          assignment.setShift(shift);
          assignment.setSchedule(assignedSchedules.getScheduleList().get(solutionCount));
          scheduledAssignments.add(assignment);
        }
      }

      solutionCount++;
    } else {
      stopSearch();
    }
  }

  public Collection<Assignment> getScheduledAssignments() {
    return scheduledAssignments;
  }

  public int getSolutionCount() {
    return solutionCount;
  }

}