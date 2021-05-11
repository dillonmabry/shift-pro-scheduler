package com.projects.shiftproscheduler.shift;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ShiftController {

    private final ShiftRepository shifts;

    public ShiftController(ShiftRepository shifts) {
        this.shifts = shifts;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping({ "/shifts" })
    public @ResponseBody Shifts getShifts() {
        Shifts shifts = new Shifts();
        shifts.getShiftsList().addAll(this.shifts.findAll());
        return shifts;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/shifts")
    Shift saveShift(@RequestBody Shift newShift) {
        return shifts.save(newShift);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/shift/{id}")
    void deleteShift(@PathVariable Integer id) {
        shifts.deleteById(id);
    }

}
