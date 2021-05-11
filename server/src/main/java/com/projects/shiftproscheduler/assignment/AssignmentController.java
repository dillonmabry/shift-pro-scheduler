package com.projects.shiftproscheduler.assignment;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
class AssignmentController {

    private final AssignmentRepository assignments;

    public AssignmentController(AssignmentRepository assignments) {
        this.assignments = assignments;
    }

    @GetMapping("/assignments")
    public @ResponseBody Assignments getAssignments() {
        Assignments assignments = new Assignments();
        assignments.getAssignmentList().addAll(this.assignments.findAll());
        return assignments;
    }

}
