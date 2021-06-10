package com.projects.shiftproscheduler.shiftday;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "shift_days")
public class ShiftDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isNew() {
        return this.id == null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
