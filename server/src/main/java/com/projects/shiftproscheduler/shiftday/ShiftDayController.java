package com.projects.shiftproscheduler.shiftday;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
class ShiftDayController {

    private final ShiftDayRepository shiftDays;

    public ShiftDayController(ShiftDayRepository shiftDays) {
        this.shiftDays = shiftDays;
    }

    @GetMapping({ "/shiftdays" })
    public @ResponseBody ShiftDays getShiftDays() {
        ShiftDays shiftDays = new ShiftDays();
        shiftDays.getShiftDaysList().addAll(this.shiftDays.findAll());
        return shiftDays;
    }

}
