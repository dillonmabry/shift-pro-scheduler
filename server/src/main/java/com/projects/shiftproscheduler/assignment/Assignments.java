package com.projects.shiftproscheduler.assignment;

import java.util.ArrayList;
import java.util.List;

public class Assignments {

    private List<Assignment> assignments;

    public List<Assignment> getAssignmentList() {
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        return assignments;
    }

}
