package com.projects.shiftproscheduler.optimizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skaggsm.ortools.OrToolsHelper;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.CpSolverStatus;
import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.assignment.AssignmentRepository;
import com.projects.shiftproscheduler.constraint.DefaultConstraintService;
import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.schedule.Schedules;
import com.projects.shiftproscheduler.shift.Shift;
import com.projects.shiftproscheduler.shift.ShiftRepository;

@Service
public class DefaultOptimizer implements IOptimizer {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    Logger logger = LoggerFactory.getLogger(DefaultOptimizer.class);

    private final DefaultConstraintService constraintService;

    public DefaultOptimizer(DefaultConstraintService constraintService) {
        OrToolsHelper.loadLibrary(); // Load Google OR Tools per maven spec
        this.constraintService = constraintService;
    }

    public Collection<Assignment> generateSchedules(Schedules schedules) throws IllegalStateException {

        CpModel model = new CpModel(); // Init model
        Collection<Employee> employees = employeeRepository.findBySupervisor(schedules.getScheduleList().get(0).getAdministrator()); // Filter by administrator
        Collection<Shift> shifts = shiftRepository.findAll();

        if(employees.size() < shifts.size()) {
            throw new IllegalStateException("Not enough employees for shifts required");
        }

        // Create shift variables
        // shift_e_d_s: employee 'e' works shift 's' on day 'd' 
        HashMap<String, IntVar> shiftVars = new HashMap<String, IntVar>();
        for (Employee employee : employees) {
            for (int d = 0; d < schedules.getScheduleList().get(0).getDays(); d++) {
                for (Shift shift : shifts) {
                    shiftVars.put(String.format("%d, %d, %d", employee.getId(), d, shift.getId()),
                            model.newBoolVar(String.format("shift_%d_%d_%d", employee.getId(), d, shift.getId())));
                }
            }
        }

        // Each shift assigned exactly MIN_EMPLOYEES per period
        for (int d = 0; d < schedules.getScheduleList().get(0).getDays(); d++) {
            for (Shift shift : shifts) {
                ArrayList<IntVar> localVars = new ArrayList<IntVar>();
                for (Employee employee : employees) {
                    IntVar shiftVar = shiftVars
                            .getOrDefault(String.format("%d, %d, %d", employee.getId(), d, shift.getId()), null);
                    if (shiftVar != null)
                        localVars.add(shiftVar);
                }
                model.addEquality(LinearExpr.sum(localVars.toArray(new IntVar[0])),
                        constraintService.getMinEmployeesPerShift());
            }
        }

        // Each employee works at most MAX_SHIFTS per day
        for (Employee employee : employees) {
            for (int d = 0; d < schedules.getScheduleList().get(0).getDays(); d++) {
                ArrayList<IntVar> localVars = new ArrayList<IntVar>();
                for (Shift shift : shifts) {
                    IntVar shiftVar = shiftVars
                            .getOrDefault(String.format("%d, %d, %d", employee.getId(), d, shift.getId()), null);
                    if (shiftVar != null)
                        localVars.add(shiftVar);
                }
                model.addLessOrEqual(LinearExpr.sum(localVars.toArray(new IntVar[0])),
                        constraintService.getMaxShiftsPerEmployee());
            }
        }

        // Distribute shifts evenly by default if possible
        int minShiftsPerEmployee = Math.floorDiv((shifts.size() * schedules.getScheduleList().get(0).getDays()),
                employees.size());
        int maxShiftsPerEmployee = 0;

        if (shifts.size() * schedules.getScheduleList().get(0).getDays() % employees.size() == 0) {
            maxShiftsPerEmployee = minShiftsPerEmployee;
        } else {
            maxShiftsPerEmployee = minShiftsPerEmployee + 1;
        }
        for (Employee employee : employees) {
            ArrayList<IntVar> localVars = new ArrayList<IntVar>();
            for (int d = 0; d < schedules.getScheduleList().get(0).getDays(); d++) {
                for (Shift shift : shifts) {
                    IntVar shiftVar = shiftVars.get(String.format("%d, %d, %d", employee.getId(), d, shift.getId()));
                    if (shiftVar != null)
                        localVars.add(shiftVar);
                }
            }
            model.addLessOrEqual(model.newConstant(minShiftsPerEmployee),
                    LinearExpr.sum(localVars.toArray(new IntVar[0])));
            model.addLessOrEqual(LinearExpr.sum(localVars.toArray(new IntVar[0])), maxShiftsPerEmployee);
        }

        // Solve
        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
            DefaultSolutionGenerator cb = new DefaultSolutionGenerator(shiftVars.values().toArray(new IntVar[0]),
                    schedules);
            solver.searchAllSolutions(model, cb);
            return cb.getScheduledAssignments();
        } else {
            logger.error("Optmizer feasibility invalid for group of employees");
            throw new IllegalStateException("Model feasibility invalid");
        }
    }

    // Solution Generator
    class DefaultSolutionGenerator extends CpSolverSolutionCallback {

        private Schedules assignedSchedules;

        private Collection<Assignment> scheduledAssignments = new ArrayList<Assignment>();

        public DefaultSolutionGenerator(IntVar[] variables, Schedules schedules) {
            variableArray = variables;
            solutionLimit = schedules.getScheduleList().size();
            assignedSchedules = schedules;
        }

        @Override
        public void onSolutionCallback() {

            for (IntVar v : variableArray) {
                String[] varValues = v.getName().split("_"); // Get constraint var values
                if (this.value(v) == 1) {
                    // Get assignment values
                    Optional<Employee> employeeUser = employeeRepository.findById(Integer.parseInt(varValues[1]));
                    Employee emp = employeeUser.orElseThrow();
                    int dayId = Integer.parseInt(varValues[2]);
                    Shift shift = shiftRepository.findById(Integer.parseInt(varValues[3])).orElseThrow();

                    Assignment assignment = new Assignment();
                    assignment.setEmployee(emp);
                    assignment.setDayId(dayId);
                    assignment.setShift(shift);
                    assignment.setSchedule(assignedSchedules.getScheduleList().get(solutionCount));
                    scheduledAssignments.add(assignment);
                }
            }

            solutionCount++;

            if (solutionCount >= solutionLimit) {
                stopSearch();
            }
        }

        public Collection<Assignment> getScheduledAssignments() {
            return scheduledAssignments;
        }

        private int solutionCount;
        private final IntVar[] variableArray;
        private final int solutionLimit;
    }
}