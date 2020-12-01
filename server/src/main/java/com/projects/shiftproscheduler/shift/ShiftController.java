package com.projects.shiftproscheduler.shift;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
class ShiftController {

    private final ShiftRepository shifts;

    public ShiftController(ShiftRepository shiftService) {
        this.shifts = shiftService;
    }

    @GetMapping({"/shifts"})
    public @ResponseBody
    Shifts getShifts() {
        Shifts shifts = new Shifts();
        shifts.getShiftsList().addAll(this.shifts.findAll());
        return shifts;
    }

}
