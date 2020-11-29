package com.projects.shiftproscheduler.administrator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.assignment.Assignments;
import com.projects.shiftproscheduler.optimizer.WeeklyOptimizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class AdministratorController {

    private final AdministratorRepository administrators;

    @Autowired
    public WeeklyOptimizer weeklyOptimizer;

    public AdministratorController(AdministratorRepository administrators) {
        this.administrators = administrators;
    }

    @GetMapping("/administrators")
    public @ResponseBody Administrators getAdministrators() {
        Administrators administrators = new Administrators();
        administrators.getAdministratorList().addAll(this.administrators.findAll());
        return administrators;
    }

    @PostMapping("/administrators/schedule/{period}")
    public @ResponseBody Assignments postScheduledAssignments(
            @PathVariable(value = "period", required = true) String period) {
        List<Assignment> scheduledAssignments = new ArrayList<Assignment>();

        switch (period) {
            case "weekly":
                scheduledAssignments.addAll(weeklyOptimizer.getSchedule());
                break;
            default:
                throw new InvalidParameterException();
        }

        Assignments assignments = new Assignments();
        assignments.getAssignmentList().addAll(scheduledAssignments);
        return assignments;
    }

}
