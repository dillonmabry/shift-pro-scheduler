package com.projects.shiftproscheduler.assignmentrequest;

import java.util.ArrayList;
import java.util.List;

public class AssignmentRequests {

    private List<AssignmentRequest> assignments;

    public List<AssignmentRequest> getAssignmentRequestList() {
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        return assignments;
    }

}
