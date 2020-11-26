package com.projects.shiftproscheduler.shift;

import java.util.ArrayList;
import java.util.List;

public class Shifts {
    private List<Shift> shifts;

    public List<Shift> getShiftsList() {
        if (shifts == null) {
            shifts = new ArrayList<>();
        }
        return shifts;
    }
}
