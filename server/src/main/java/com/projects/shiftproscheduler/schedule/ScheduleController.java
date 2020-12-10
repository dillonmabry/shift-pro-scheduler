package com.projects.shiftproscheduler.schedule;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public @ResponseBody Schedule getSchedule(@PathVariable(value = "id", required = true) Integer id) {
        return this.schedules.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

    @DeleteMapping("/schedule/{id}")
    public @ResponseBody void deleteSchedule(@PathVariable(value = "id", required = true) Integer id) {
        this.schedules.deleteById(id);
    }

    @PostMapping("/schedule/{id}")
    public @ResponseBody void activateSchedule(@PathVariable(value = "id", required = true) Integer id) {
        Schedule schedule = schedules.findById(id).get();
        schedule.setIsActive(true);
        schedules.save(schedule);
    }

}
