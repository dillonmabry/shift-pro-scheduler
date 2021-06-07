package com.projects.shiftproscheduler.assignment;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AssignmentController {

    private final AssignmentRepository assignments;

    public AssignmentController(AssignmentRepository assignments) {
        this.assignments = assignments;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/assignments")
    public @ResponseBody Assignments getAssignments() {
        Assignments assignments = new Assignments();
        assignments.getAssignmentList().addAll(this.assignments.findAll());
        return assignments;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/assignments")
    Assignment saveAssignmentRequest(@RequestBody Assignment newAssignment) {
        return assignments.save(newAssignment);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/assignment/{id}")
    void deleteAssignment(@PathVariable Integer id) {
        assignments.deleteById(id);
    }

}
