package com.projects.shiftproscheduler.schedule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.administrator.AdministratorRepository;
import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.assignment.AssignmentRepository;
import com.projects.shiftproscheduler.optimizer.DefaultOptimizer;
import com.projects.shiftproscheduler.optimizer.IOptimizer;
import com.projects.shiftproscheduler.optimizer.PreferenceOptimizer;
import com.projects.shiftproscheduler.security.ErrorInfo;
import com.projects.shiftproscheduler.security.JWTUtil;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ScheduleController {

    private final ScheduleRepository schedules;

    private final AdministratorRepository administrators;

    private final AssignmentRepository assignments;

    @Autowired
    public DefaultOptimizer defaultOptimizer;

    @Autowired
    public PreferenceOptimizer preferenceOptimizer;

    @Autowired
    private ApplicationContext applicationContext;

    public ScheduleController(ScheduleRepository schedules, AdministratorRepository administrators,
            AssignmentRepository assignments) {
        this.schedules = schedules;
        this.administrators = administrators;
        this.assignments = assignments;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/schedules")
    public @ResponseBody Schedules getSchedules() {
        Schedules schedules = new Schedules();
        schedules.getScheduleList().addAll(this.schedules.findAll());
        return schedules;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/schedule/{id}")
    public @ResponseBody Schedule getSchedule(@PathVariable(value = "id", required = true) Integer id) {
        return this.schedules.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/schedule/{id}")
    public @ResponseBody void deleteSchedule(@PathVariable(value = "id", required = true) Integer id) {
        this.schedules.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/schedule/{id}")
    public @ResponseBody void activateSchedule(@PathVariable(value = "id", required = true) Integer id) {
        Schedule schedule = schedules.findById(id).get();
        Optional<Collection<Schedule>> activeSchedules = schedules.findAllByIsActive(true);

        activeSchedules.ifPresentOrElse(s -> {
            Collection<Schedule> filteredSchedules = s.stream()
                    .filter(p -> schedule.getStartDate().compareTo(p.getEndDate()) <= 0
                            && p.getStartDate().compareTo(schedule.getEndDate()) <= 0)
                    .collect(Collectors.toList());

            if (filteredSchedules.size() > 0)
                throw new EntityExistsException();

            schedule.setIsActive(true);
            schedules.save(schedule);
        }, () -> {
            schedule.setIsActive(true);
            schedules.save(schedule);
        });
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/schedules/{optimizer}/{numSchedules}/{startDate}/{endDate}")
    @Transactional
    public @ResponseBody Schedules postScheduledAssignments(@RequestHeader("Authorization") String token,
            @PathVariable(value = "optimizer", required = true) String optimizer,
            @PathVariable(value = "numSchedules", required = true) @Range(min = 1, max = 10, message = "Number of schedules created must be between 1 and 10") Integer numSchedules,
            @PathVariable(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (!applicationContext.containsBean(optimizer))
            throw new IllegalArgumentException("No optimizer available for input provided");

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

        IOptimizer selectedOptimizer = (IOptimizer) applicationContext.getBean(optimizer);
        scheduledAssignments.addAll(selectedOptimizer.generateSchedules(assignedSchedules));

        assignments.saveAll(scheduledAssignments);

        return assignedSchedules;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityExistsException.class)
    @ResponseBody
    ErrorInfo duplicateActiveScheduleException(HttpServletRequest req, EntityExistsException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex,
                "Active schedule with date range selected already exists");
    }

}
