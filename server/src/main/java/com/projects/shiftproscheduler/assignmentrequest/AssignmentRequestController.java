package com.projects.shiftproscheduler.assignmentrequest;

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
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
class AssignmentRequestController {

    private final AssignmentRequestRepository assignmentRequests;

    public AssignmentRequestController(AssignmentRequestRepository assignmentRequests) {
        this.assignmentRequests = assignmentRequests;
    }

    @GetMapping("/assignmentrequests")
    public @ResponseBody AssignmentRequests getAssignmentRequests() {
        AssignmentRequests assignmentRequests = new AssignmentRequests();
        assignmentRequests.getAssignmentRequestList().addAll(this.assignmentRequests.findAll());
        return assignmentRequests;
    }

    @PostMapping("/assignmentrequests")
    AssignmentRequest saveAssignmentRequest(@RequestBody AssignmentRequest newAssignmentRequest) {
        return assignmentRequests.save(newAssignmentRequest);
    }

    @DeleteMapping("/assignmentrequest/{id}")
    void deleteAssignmentRequest(@PathVariable Integer id) {
        assignmentRequests.deleteById(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    ErrorInfo duplicateAssignmentRequestException(HttpServletRequest req, DataIntegrityViolationException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex,
                "User(s) already exist with AssignmentRequest specified cannot take action. First remove Employee(s) with associated AssignmentRequest.");
    }

}
