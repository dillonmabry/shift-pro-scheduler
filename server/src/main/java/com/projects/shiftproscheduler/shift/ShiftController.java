package com.projects.shiftproscheduler.shift;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import com.projects.shiftproscheduler.security.ErrorInfo;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    ErrorInfo duplicateUserNameException(HttpServletRequest req, DataIntegrityViolationException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex,
                "Assignment(s) already exist with Shift specified cannot take action. First remove Schedule(s) with associated Shift.");
    }

}
