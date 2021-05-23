package com.projects.shiftproscheduler.administrator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.assignment.AssignmentRepository;
import com.projects.shiftproscheduler.assignment.Assignments;
import com.projects.shiftproscheduler.optimizer.DefaultOptimizer;
import com.projects.shiftproscheduler.schedule.Schedule;
import com.projects.shiftproscheduler.schedule.ScheduleRepository;
import com.projects.shiftproscheduler.schedule.Schedules;
import com.projects.shiftproscheduler.security.ErrorInfo;
import com.projects.shiftproscheduler.security.JWTUtil;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@PreAuthorize("hasRole('ADMIN')")
class AdministratorController {

    private final AdministratorRepository administrators;
    private final ScheduleRepository schedules;
    private final AssignmentRepository assignments;

    @Autowired
    public DefaultOptimizer defaultOptimizer;

    public AdministratorController(AdministratorRepository administrators, ScheduleRepository schedules,
            AssignmentRepository assignments) {
        this.administrators = administrators;
        this.schedules = schedules;
        this.assignments = assignments;
    }

    @GetMapping("/administrators")
    public @ResponseBody Administrators getAdministrators() {
        Administrators administrators = new Administrators();
        administrators.getAdministratorList().addAll(this.administrators.findAll());
        return administrators;
    }

    @GetMapping("/administrator/{username}")
    public @ResponseBody Administrator getAdministrator(
            @PathVariable(value = "username", required = true) String username) {
        return administrators.findByUserName(username).orElseThrow(() -> new EntityNotFoundException());
    }

    @PostMapping("/administrators/{username}")
    Administrator saveAdministrator(@RequestBody Administrator newAdministrator,
            @RequestHeader("Authorization") String token) {
        administrators.findById(newAdministrator.getId()).orElseThrow();
        UsernamePasswordAuthenticationToken authToken = JWTUtil.parseToken(token.split(" ")[1]);
        if (authToken == null || !authToken.getName().equals(newAdministrator.getUserName()))
            throw new AccessDeniedException("Authenticated user does not have access to save employee details");
        return administrators.save(newAdministrator);
    }

    @PostMapping("/administrators/schedules/{numSchedules}/{startDate}/{endDate}")
    @Transactional
    public @ResponseBody Assignments postScheduledAssignments(@RequestHeader("Authorization") String token,
            @PathVariable(value = "numSchedules", required = true) @Range(min = 1, max = 10, message = "Number of schedules created must be between 1 and 10") Integer numSchedules,
            @PathVariable(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        UsernamePasswordAuthenticationToken authToken = JWTUtil.parseToken(token.split(" ")[1]);
        if (authToken == null)
            throw new AccessDeniedException("Authenticated user does not have access to post new schedules");

        Administrator administrator = administrators.findByUserName(authToken.getName())
                .orElseThrow(() -> new EntityNotFoundException());

        Schedules assignedSchedules = new Schedules();
        IntStream.range(0, numSchedules).forEach(i -> {
            Schedule schedule = new Schedule();
            schedule.setAdministrator(administrator);
            schedule.setStartDate(startDate);
            schedule.setEndDate(endDate);
            assignedSchedules.getScheduleList().add(schedule);
        });
        schedules.saveAll(assignedSchedules.getScheduleList());

        List<Assignment> scheduledAssignments = new ArrayList<Assignment>();
        scheduledAssignments.addAll(defaultOptimizer.generateSchedules(assignedSchedules));
        assignments.saveAll(scheduledAssignments);

        Assignments assignments = new Assignments();
        assignments.getAssignmentList().addAll(scheduledAssignments);
        return assignments;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    ErrorInfo illegalScheduleStateException(HttpServletRequest req, IllegalStateException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex,
                "Model cannot be generated with available employees and shifts with date range");
    }

}
