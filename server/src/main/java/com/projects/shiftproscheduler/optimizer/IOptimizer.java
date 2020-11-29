package com.projects.shiftproscheduler.optimizer;

import java.util.Collection;

import com.projects.shiftproscheduler.assignment.Assignment;

public interface IOptimizer {

    Collection<Assignment> getSchedule();
}
