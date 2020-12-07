package com.projects.shiftproscheduler.administrator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.assignment.AssignmentRepository;
import com.projects.shiftproscheduler.assignment.Assignments;
import com.projects.shiftproscheduler.optimizer.DefaultOptimizer;
import com.projects.shiftproscheduler.schedule.Schedule;
import com.projects.shiftproscheduler.schedule.ScheduleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
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
    public @ResponseBody Administrator getAdministrator(@PathVariable(value = "username", required = true) String username) {
        return this.administrators.findByUserName(username).orElseThrow(() -> new EntityNotFoundException());
    }

    @PostMapping("/administrators/schedule/{days}")
    @Transactional
    public @ResponseBody Assignments postScheduledAssignments(
            @PathVariable(value = "days", required = true) Integer days) {

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Administrator administrator = administrators.findByUserName(username).orElseThrow(() -> new EntityNotFoundException());

        Schedule schedule = new Schedule();
        schedule.setAdministrator(administrator);
        schedule.setCreatedAt(new Date());
        schedule.setDays(days);
        schedules.save(schedule);

        List<Assignment> scheduledAssignments = new ArrayList<Assignment>();
        scheduledAssignments.addAll(defaultOptimizer.generateSchedule(schedule));
        assignments.saveAll(scheduledAssignments);

        Assignments assignments = new Assignments();
        assignments.getAssignmentList().addAll(scheduledAssignments);
        return assignments;
    }

}
