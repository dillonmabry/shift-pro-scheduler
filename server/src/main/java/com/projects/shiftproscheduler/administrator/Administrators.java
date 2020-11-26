package com.projects.shiftproscheduler.administrator;

import java.util.ArrayList;
import java.util.List;

public class Administrators {

    private List<Administrator> administrators;

    public List<Administrator> getAdministratorList() {
        if (administrators == null) {
            administrators = new ArrayList<>();
        }
        return administrators;
    }

}
