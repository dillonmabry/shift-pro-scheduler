package com.projects.shiftproscheduler.shift;

import java.sql.Time;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.projects.shiftproscheduler.assignment.Assignment;

@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "shift")
    private Set<Assignment> assignments = new HashSet<Assignment>();

    @Column(name = "start_time")
    @NotNull
    private Time startTime;

    @Column(name = "end_time")
    @NotNull
    private Time endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isNew() {
        return this.id == null;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("Start Time: %s, End Time: %s", startTime, endTime);
    }
}
