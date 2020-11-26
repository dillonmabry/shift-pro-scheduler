package com.projects.shiftproscheduler.optimizer;

import com.skaggsm.ortools.OrToolsHelper;

import java.util.Collection;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.sat.CpModel;
import com.projects.shiftproscheduler.employee.Employees;

public class Optimizer {

    private Collection<Employees> employees;

    public Optimizer() {
        OrToolsHelper.loadLibrary();
    }

    public void testOptimize() {
        // Create CP Model
        CpModel model = new CpModel();
    }
}