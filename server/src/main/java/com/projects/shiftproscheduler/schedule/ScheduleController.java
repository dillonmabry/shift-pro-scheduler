package com.projects.shiftproscheduler.schedule;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import com.projects.shiftproscheduler.security.ErrorInfo;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ScheduleController {

    private final ScheduleRepository schedules;

    public ScheduleController(ScheduleRepository schedules) {
        this.schedules = schedules;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityExistsException.class)
    @ResponseBody
    ErrorInfo duplicateActiveScheduleException(HttpServletRequest req, EntityExistsException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex,
                "Active schedule with date range selected already exists");
    }

}
