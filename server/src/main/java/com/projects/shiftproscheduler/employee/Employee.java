package com.projects.shiftproscheduler.employee;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.department.Department;

@Entity
@Table(name = "employees")
public class Employee extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "employee")
    private Set<Assignment> assignments = new HashSet<Assignment>();

    @ManyToOne
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public boolean isNew() {
        return this.id == null;
    }

}
