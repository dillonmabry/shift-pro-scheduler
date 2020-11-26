package com.projects.shiftproscheduler.employee;

import java.util.Set;

import javax.persistence.*;

import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.shift.Shift;

@Entity
@Table(name = "employees")
public class Employee extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany
    @JoinTable(name = "assignments", joinColumns = @JoinColumn(name = "emp_id"), inverseJoinColumns = @JoinColumn(name = "shift_id"))
    Set<Shift> shifts;

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
