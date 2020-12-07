package com.projects.shiftproscheduler.schedule;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.assignment.Assignment;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Administrator administrator;

    @OneToMany(mappedBy = "schedule")
    @JsonIgnoreProperties("schedule")
    private Set<Assignment> assignments;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "days")
    private Integer days;

    @Transient
    private Date endDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public boolean isNew() {
        return this.id == null;
    }

    public Set<Assignment> getAssignments() {
        return assignments;
    }

    public Date getEndDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
}