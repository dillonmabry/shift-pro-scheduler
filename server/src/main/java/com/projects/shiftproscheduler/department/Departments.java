package com.projects.shiftproscheduler.department;

import java.util.ArrayList;
import java.util.List;

public class Departments {
    private List<Department> departments;

    public List<Department> getDepartmentsList() {
        if (departments == null) {
            departments = new ArrayList<>();
        }
        return departments;
    }

}
