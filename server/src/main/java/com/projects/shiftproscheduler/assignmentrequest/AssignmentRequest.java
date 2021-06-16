package com.projects.shiftproscheduler.assignmentrequest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.projects.shiftproscheduler.employee.Employee;
import com.projects.shiftproscheduler.shift.Shift;
import com.projects.shiftproscheduler.shiftday.ShiftDay;

@Entity
@Table(name = "assignment_requests")
public class AssignmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "day_id")
    private ShiftDay shiftDay;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isNew() {
        return this.id == null;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public ShiftDay getShiftDay() {
        return shiftDay;
    }

    public void setShiftDay(ShiftDay shiftDay) {
        this.shiftDay = shiftDay;
    }

    @Override
    public boolean equals(Object obj) {
        AssignmentRequest req = (AssignmentRequest) obj;
        return this.getEmployee().getId().equals(req.getEmployee().getId())
                && this.getShift().getId().equals(req.getShift().getId())
                && this.getShiftDay().getId().equals(req.getShiftDay().getId());
    }

    @Override
    public int hashCode() {
        return this.getEmployee().getId().hashCode() + this.getShift().getId().hashCode()
                + this.getShiftDay().getId().hashCode();
    }

}