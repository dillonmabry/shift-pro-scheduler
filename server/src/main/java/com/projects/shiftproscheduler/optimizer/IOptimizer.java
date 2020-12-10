package com.projects.shiftproscheduler.optimizer;

import java.util.Collection;

import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.schedule.Schedules;

public interface IOptimizer {

    Collection<Assignment> generateSchedules(Schedules schedules);
}
