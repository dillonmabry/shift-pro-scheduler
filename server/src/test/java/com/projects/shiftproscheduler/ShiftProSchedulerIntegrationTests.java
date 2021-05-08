package com.projects.shiftproscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Collection;

import com.projects.shiftproscheduler.administrator.AdministratorRepository;
import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.department.DepartmentRepository;
import com.projects.shiftproscheduler.employee.EmployeeRepository;
import com.projects.shiftproscheduler.optimizer.DefaultOptimizer;
import com.projects.shiftproscheduler.schedule.Schedule;
import com.projects.shiftproscheduler.schedule.ScheduleRepository;
import com.projects.shiftproscheduler.schedule.Schedules;
import com.projects.shiftproscheduler.shift.ShiftRepository;

@SpringBootTest
@AutoConfigureTestDatabase
public class ShiftProSchedulerIntegrationTests {

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private AdministratorRepository administrators;

    @Autowired
    private ScheduleRepository schedules;

    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    private DepartmentRepository departments;

    @Autowired
    private DefaultOptimizer optimizer;

    @Test
    public void testFindAll() throws Exception {
        employees.findAll();
        employees.findAll(); // served from cache
    }

    @Test
    void testDefaultOptimizer() throws Exception {

        Department department = new Department();
        department.setName("Supplies");
        departments.save(department);

        Schedule schedule = new Schedule();
        schedule.setAdministrator(administrators.findByUserName("admin").orElseThrow());
        schedule.setStartDate(LocalDate.now());
        schedule.setEndDate(LocalDate.now().plusDays(7));
        schedules.save(schedule);

        assertEquals(schedules.findAll().size(), 1);
        assertEquals(7, schedule.getDays());
        assertEquals(shiftRepository.findAll().size(), 3);
        assertEquals(employees.findAll().size(), 4);

        Schedules schedules = new Schedules();
        schedules.getScheduleList().add(schedule);
        Collection<Assignment> assignments = optimizer.generateSchedules(schedules);
        assertEquals(21, assignments.size());
    }


}