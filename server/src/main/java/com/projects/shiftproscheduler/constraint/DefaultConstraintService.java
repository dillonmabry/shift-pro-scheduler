package com.projects.shiftproscheduler.constraint;

import org.springframework.stereotype.Service;

@Service
public class DefaultConstraintService extends IConstraintService {

    private final int numDays = 7;
    private final int maxShifts = 1;
    private final int minShifts = 1;

    public DefaultConstraintService() {

    }

    public int getNumberOfDays() {
        return numDays;
    }

    public int getMaxShiftsPerEmployee() {
        return maxShifts;
    }

    public int getMinEmployeesPerShift() {
        return minShifts;
    }
}
