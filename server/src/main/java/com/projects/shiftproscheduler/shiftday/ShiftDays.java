package com.projects.shiftproscheduler.shiftday;

import java.util.ArrayList;
import java.util.List;

public class ShiftDays {
    private List<ShiftDay> shiftDays;

    public List<ShiftDay> getShiftDaysList() {
        if (shiftDays == null) {
            shiftDays = new ArrayList<>();
        }
        return shiftDays;
    }
}
