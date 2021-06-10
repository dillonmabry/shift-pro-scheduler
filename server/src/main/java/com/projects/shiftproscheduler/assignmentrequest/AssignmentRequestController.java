package com.projects.shiftproscheduler.assignmentrequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;

import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.security.JWTUtil;
import com.projects.shiftproscheduler.shift.ShiftRepository;
import com.projects.shiftproscheduler.shiftday.ShiftDayRepository;

@RestController
class AssignmentRequestController {

    private final AssignmentRequestRepository assignmentRequests;

    private final EmployeeRepository employees;

    private final ShiftRepository shifts;

    private final ShiftDayRepository shiftDays;

    public AssignmentRequestController(AssignmentRequestRepository assignmentRequests, EmployeeRepository employees,
            ShiftRepository shifts, ShiftDayRepository shiftDays) {
        this.assignmentRequests = assignmentRequests;
        this.employees = employees;
        this.shifts = shifts;
        this.shiftDays = shiftDays;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/assignmentrequests")
    public @ResponseBody AssignmentRequests getAssignmentRequests() {
        AssignmentRequests assignmentRequests = new AssignmentRequests();
        assignmentRequests.getAssignmentRequestList().addAll(this.assignmentRequests.findAll());
        return assignmentRequests;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/assignmentrequests/{username}")
    public @ResponseBody AssignmentRequests getAssignmentRequestsByUser(
            @PathVariable(value = "username", required = true) String username,
            @RequestHeader("Authorization") String token) {
        UsernamePasswordAuthenticationToken authToken = JWTUtil.parseToken(token.split(" ")[1]);
        Employee employee = this.employees.findByUserName(authToken.getName())
                .orElseThrow(() -> new EntityNotFoundException());
        if (authToken == null || !authToken.getName().equals(employee.getUserName()))
            throw new AccessDeniedException("Authenticated user does not have access to view assignment requests");

        AssignmentRequests assignmentRequests = new AssignmentRequests();
        assignmentRequests.getAssignmentRequestList().addAll(this.assignmentRequests.findByEmployee(employee));
        return assignmentRequests;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/assignmentrequests")
    AssignmentRequest saveAssignmentRequest(@RequestBody AssignmentRequest newRequest,
            @RequestHeader("Authorization") String token) {
        UsernamePasswordAuthenticationToken authToken = JWTUtil.parseToken(token.split(" ")[1]);
        Employee employee = employees.findByUserName(authToken.getName())
                .orElseThrow(() -> new EntityNotFoundException());
        if (authToken == null || !authToken.getName().equals(employee.getUserName()))
            throw new AccessDeniedException("Authenticated user does not have access to save assignment request");

        // Set employee
        newRequest.setEmployee(employee);

        // Set default shift
        if (newRequest.getShift() == null)
            newRequest.setShift(shifts.findAll().iterator().next());

        // Set default shift day
        if (newRequest.getShiftDay() == null)
            newRequest.setShiftDay(shiftDays.findAll().iterator().next());

        return assignmentRequests.save(newRequest);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @DeleteMapping("/assignmentrequest/{id}")
    void deleteAssignmentRequest(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        UsernamePasswordAuthenticationToken authToken = JWTUtil.parseToken(token.split(" ")[1]);
        Employee employee = employees.findByUserName(authToken.getName())
                .orElseThrow(() -> new EntityNotFoundException());
        if (authToken == null || !authToken.getName().equals(employee.getUserName()))
            throw new AccessDeniedException("Authenticated user does not have access to delete assignment request");

        assignmentRequests.deleteById(id);
    }

}
