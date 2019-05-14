package com.projects.shiftproscheduler;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiftProSchedulerIntegrationTests {

    @Autowired
    private EmployeeRepository employees;

    @Test
    public void testFindAll() throws Exception {
        employees.findAll();
        employees.findAll(); // served from cache
    }
}