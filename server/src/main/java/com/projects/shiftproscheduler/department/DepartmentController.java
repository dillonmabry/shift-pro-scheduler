package com.projects.shiftproscheduler.department;

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
public class DepartmentController {

    private final DepartmentRepository departments;

    public DepartmentController(DepartmentRepository departments) {
        this.departments = departments;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping({ "/departments" })
    public @ResponseBody Departments getDepartments() {
        Departments departments = new Departments();
        departments.getDepartmentsList().addAll(this.departments.findAll());
        return departments;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/departments")
    Department saveDepartment(@RequestBody Department newDepartment) {
        return departments.save(newDepartment);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/department/{id}")
    void deleteDepartment(@PathVariable Integer id) {
        departments.deleteById(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    ErrorInfo duplicateUserNameException(HttpServletRequest req, DataIntegrityViolationException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex,
                "User(s) already exist with Department specified cannot take action. First remove Employee(s) with associated Department.");
    }
}
