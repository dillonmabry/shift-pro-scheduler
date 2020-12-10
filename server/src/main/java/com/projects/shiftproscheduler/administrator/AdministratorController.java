package com.projects.shiftproscheduler.administrator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityNotFoundException;

import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.assignment.AssignmentRepository;
import com.projects.shiftproscheduler.assignment.Assignments;
import com.projects.shiftproscheduler.optimizer.DefaultOptimizer;
import com.projects.shiftproscheduler.schedule.Schedule;
import com.projects.shiftproscheduler.schedule.ScheduleRepository;
import com.projects.shiftproscheduler.schedule.Schedules;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
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
        return this.administrators.findByUserName(username).orElseThrow(() -> new EntityNotFoundException());
    }

    @PostMapping("/administrators/schedules")
    @Transactional
    public @ResponseBody Assignments postScheduledAssignments(
            @RequestParam(value = "numSchedules", required = true) @Range(min = 1, max = 10, message = "Number of schedules created must be between 1 and 10") Integer numSchedules,
            @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Administrator administrator = administrators.findByUserName(username)
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

}
