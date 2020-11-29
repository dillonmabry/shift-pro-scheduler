package com.projects.shiftproscheduler.optimizer;

import com.skaggsm.ortools.OrToolsHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.CpSolverStatus;
import com.projects.shiftproscheduler.constraint.DefaultConstraintService;
import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.shift.Shift;
import com.projects.shiftproscheduler.shift.ShiftRepository;

@Service
public class WeeklyOptimizer implements IOptimizer {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    ShiftRepository shiftRepository;

    private final DefaultConstraintService constraintService;

    public WeeklyOptimizer(DefaultConstraintService constraintService) {
        OrToolsHelper.loadLibrary(); // Load Google OR Tools per maven spec
        this.constraintService = constraintService;
    }

    public Collection<Employee> getSchedule() throws IllegalStateException {

        CpModel model = new CpModel(); // Init model
        Collection<Employee> employees = employeeRepository.findAll();
        Collection<Shift> shifts = shiftRepository.findAll();

        // Create shift variables
        // shift_e_d_s: employee 'e' works shift 's' on day 'd'
        HashMap<String, IntVar> shiftVars = new HashMap<String, IntVar>();
        for (Employee employee : employees) {
            for (int d = 0; d < constraintService.getNumberOfDays(); d++) {
                for (Shift shift : shifts) {
                    shiftVars.put(String.format("%d, %d, %d", employee.getId(), d, shift.getId()),
                            model.newBoolVar(String.format("shift_%d_%d_%d", employee.getId(), d, shift.getId())));
                }
            }
        }

        // Each shift assigned exactly MIN_EMPLOYEES per period
        for (int d = 0; d < constraintService.getNumberOfDays(); d++) {
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
            for (int d = 0; d < constraintService.getNumberOfDays(); d++) {
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
        int minShiftsPerEmployee = Math.floorDiv((shifts.size() * constraintService.getNumberOfDays()),
                employees.size());
        int maxShiftsPerEmployee = 0;

        if (shifts.size() * constraintService.getNumberOfDays() % employees.size() == 0) {
            maxShiftsPerEmployee = minShiftsPerEmployee;
        } else {
            maxShiftsPerEmployee = minShiftsPerEmployee + 1;
        }
        for (Employee employee : employees) {
            ArrayList<IntVar> localVars = new ArrayList<IntVar>();
            for (int d = 0; d < constraintService.getNumberOfDays(); d++) {
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
            // VarArraySolutionPrinter cb = new
            // VarArraySolutionPrinter(shiftVars.values().toArray(new IntVar[0]));
            // solver.searchAllSolutions(model, cb);
            // System.out.println(cb.solutionCount);
            for (IntVar v : shiftVars.values().toArray(new IntVar[0])) {
                System.out.printf(" %s = %d%n", v.getName(), solver.value(v));
                // String[] varValues = v.getName().split("_");
                // if (solver.value(v) == 1) {
                //     Employee emp = employeeRepository.findById(Integer.parseInt(varValues[1]));
                //     emp.setShifts(shifts);
                // } else {

                // }
            }
        } else {
            throw new IllegalStateException("Model feasibility invalid");
        }

        return employees;
    }

    static class VarArraySolutionPrinter extends CpSolverSolutionCallback {

        private int solutionCount;
        private final IntVar[] variableArray;

        public VarArraySolutionPrinter(IntVar[] variables) {
            variableArray = variables;
        }

        @Override
        public void onSolutionCallback() {
            System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
            for (IntVar v : variableArray) {
                System.out.printf(" %s = %d%n", v.getName(), value(v));
            }

            solutionCount++;
        }

        public int getSolutionCount() {
            return solutionCount;
        }
    }
}