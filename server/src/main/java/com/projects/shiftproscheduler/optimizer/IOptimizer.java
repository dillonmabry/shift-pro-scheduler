package com.projects.shiftproscheduler.optimizer;

import java.util.Collection;

import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.schedule.Schedule;

public interface IOptimizer {

    Collection<Assignment> generateSchedule(Schedule schedule);
}
