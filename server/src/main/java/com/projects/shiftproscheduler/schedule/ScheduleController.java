package com.projects.shiftproscheduler.schedule;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
class ScheduleController {

    private final ScheduleRepository schedules;

    public ScheduleController(ScheduleRepository schedules) {
        this.schedules = schedules;
    }

    @GetMapping("/schedules")
    public @ResponseBody Schedules getSchedules() {
        Schedules schedules = new Schedules();
        schedules.getScheduleList().addAll(this.schedules.findAll());
        return schedules;
    }

    @GetMapping("/schedule/{id}")
    public @ResponseBody Schedule getSchedule(@PathVariable(value = "id", required = true) int id) {
        return this.schedules.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

}
