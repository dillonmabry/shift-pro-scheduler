package com.projects.shiftproscheduler.department;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
}
