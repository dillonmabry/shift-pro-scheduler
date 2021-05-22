package com.projects.shiftproscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Time;
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
import com.projects.shiftproscheduler.shift.Shift;
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
    ShiftRepository shifts;

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

        assertEquals(1, schedules.findAll().size());
        assertEquals(7, schedule.getDays());
        assertEquals(3, shifts.findAll().size());
        assertEquals(4, employees.findAll().size());

        Schedules schedules = new Schedules();
        schedules.getScheduleList().add(schedule);
        Collection<Assignment> assignments = optimizer.generateSchedules(schedules);
        assertEquals(21, assignments.size());
    }

    @Test()
    void testDefaultOptimizerInvalidModel() {

        Shift s1 = new Shift();
        s1.setStartTime(Time.valueOf("00:00:00"));
        s1.setEndTime(Time.valueOf("08:00:00"));

        Shift s2 = new Shift();
        s2.setStartTime(Time.valueOf("10:00:00"));
        s2.setEndTime(Time.valueOf("14:00:00"));

        shifts.save(s1);
        shifts.save(s2);

        Schedule schedule = new Schedule();
        schedule.setAdministrator(administrators.findByUserName("admin").orElseThrow());
        schedule.setStartDate(LocalDate.now());
        schedule.setEndDate(LocalDate.now().plusDays(7));
        schedules.save(schedule);

        Schedules schedules = new Schedules();
        schedules.getScheduleList().add(schedule);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            optimizer.generateSchedules(schedules);
        });

        assertEquals("Not enough employees for shifts required", exception.getMessage());
    }

}