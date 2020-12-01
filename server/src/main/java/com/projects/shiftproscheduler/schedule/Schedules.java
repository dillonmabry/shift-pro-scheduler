package com.projects.shiftproscheduler.schedule;

import java.util.ArrayList;
import java.util.List;

public class Schedules {

    private List<Schedule> schedules;

    public List<Schedule> getScheduleList() {
        if (schedules == null) {
            schedules = new ArrayList<>();
        }
        return schedules;
    }

}
