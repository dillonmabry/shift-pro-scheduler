package com.projects.shiftproscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.stream.Collectors;

import com.projects.shiftproscheduler.administrator.Administrator;
import com.projects.shiftproscheduler.administrator.AdministratorRepository;
import com.projects.shiftproscheduler.assignment.Assignment;
import com.projects.shiftproscheduler.department.Department;
import com.projects.shiftproscheduler.department.DepartmentRepository;
import com.projects.shiftproscheduler.employee.Employee;
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

    @Test void testDefaultOptimizer() throws Exception {

        Department department = new Department();
        department.setName("Supplies");
        departments.save(department);

        Administrator administrator = new Administrator();
        administrator.setFirstName("admin");
        administrator.setLastName("admin");
        administrator.setEmail("admin@gmail.com");
        administrator.setUserName("admin");
        administrator.setDepartment(departments.findByName("Supplies").orElseThrow());
        administrator.setPhone("000-000-0000");
        administrators.save(administrator);

        Schedule schedule = new Schedule();
        schedule.setAdministrator(administrators.findByUserName("admin").orElseThrow());
        schedule.setStartDate(LocalDate.now());
        schedule.setEndDate(LocalDate.now().plusDays(7));
        schedules.save(schedule);

        assertEquals(1, schedules.findAll().size());
        Schedule foundSchedule = schedules.findById(1).orElseThrow();
        assertEquals(7, foundSchedule.getDays());

        Shift shift1 = new Shift();
        shift1.setStartTime(Time.valueOf(LocalTime.now()));
        shift1.setEndTime(Time.valueOf(LocalTime.now().plusHours(3)));
        Shift shift2 = new Shift();
        shift2.setStartTime(Time.valueOf(LocalTime.now()));
        shift2.setEndTime(Time.valueOf(LocalTime.now().plusHours(3)));
        Shift shift3 = new Shift();
        shift3.setStartTime(Time.valueOf(LocalTime.now()));
        shift3.setEndTime(Time.valueOf(LocalTime.now().plusHours(3)));
        shiftRepository.save(shift1);
        shiftRepository.save(shift2);
        shiftRepository.save(shift3);
        assertEquals(3, shiftRepository.findAll().size());

        Employee emp1 = new Employee();
        emp1.setFirstName("James");
        emp1.setLastName("Carter");
        emp1.setEmail("jcarter@gmail.com");
        emp1.setUserName("jcarter");
        emp1.setDepartment(departments.findByName("Supplies").orElseThrow());
        emp1.setPhone("999-999-9999");
        emp1.setSupervisor(administrator);
        employees.save(emp1);

        Employee emp2 = new Employee();
        emp2.setFirstName("Helen");
        emp2.setLastName("Leary");
        emp2.setEmail("hleary@gmail.com");
        emp2.setUserName("hleary");
        emp2.setDepartment(departments.findByName("Supplies").orElseThrow());
        emp2.setPhone("999-999-9999");
        emp2.setSupervisor(administrator);
        employees.save(emp2);

        Employee emp3 = new Employee();
        emp3.setFirstName("John");
        emp3.setLastName("Smith");
        emp3.setEmail("jsmith@gmail.com");
        emp3.setUserName("jsmith");
        emp3.setDepartment(departments.findByName("Supplies").orElseThrow());
        emp3.setPhone("999-999-9999");
        emp3.setSupervisor(administrator);
        employees.save(emp3);

        Employee emp4 = new Employee();
        emp4.setFirstName("Adam");
        emp4.setLastName("Thomas");
        emp4.setEmail("athomas@gmail.com");
        emp4.setUserName("athomas");
        emp4.setDepartment(departments.findByName("Supplies").orElseThrow());
        emp4.setPhone("999-999-9999");
        emp4.setSupervisor(administrator);
        employees.save(emp4);

        assertEquals(4, employees.findAll().size());

        Schedules schedules = new Schedules();
        schedules.getScheduleList().add(schedule);

        Collection<Assignment> assignments = optimizer.generateSchedules(schedules);
        assertEquals(21, assignments.size());
    }

    @Test void testDefaultOptimizerByAdmin() throws Exception {

        Administrator administrator = new Administrator();
        administrator.setFirstName("James");
        administrator.setLastName("Dean");
        administrator.setEmail("jdean@gmail.com");
        administrator.setUserName("jdean");
        administrator.setDepartment(departments.findByName("Supplies").orElseThrow());
        administrator.setPhone("000-000-0000");
        administrators.save(administrator);

        Schedule schedule = new Schedule();
        schedule.setAdministrator(administrator);
        schedule.setStartDate(LocalDate.now());
        schedule.setEndDate(LocalDate.now().plusDays(7));
        schedules.save(schedule);

        Employee emp1 = new Employee();
        emp1.setFirstName("James");
        emp1.setLastName("Carter");
        emp1.setEmail("jcarter1@gmail.com");
        emp1.setUserName("jcarter1");
        emp1.setDepartment(departments.findByName("Supplies").orElseThrow());
        emp1.setPhone("999-999-9999");
        emp1.setSupervisor(administrator);
        employees.save(emp1);

        Employee emp2 = new Employee();
        emp2.setFirstName("Helen");
        emp2.setLastName("Leary");
        emp2.setEmail("hleary1@gmail.com");
        emp2.setUserName("hleary1");
        emp2.setDepartment(departments.findByName("Supplies").orElseThrow());
        emp2.setPhone("999-999-9999");
        emp2.setSupervisor(administrator);
        employees.save(emp2);

        Employee emp3 = new Employee();
        emp3.setFirstName("John");
        emp3.setLastName("Smith");
        emp3.setEmail("jsmith1@gmail.com");
        emp3.setUserName("jsmith1");
        emp3.setDepartment(departments.findByName("Supplies").orElseThrow());
        emp3.setPhone("999-999-9999");
        emp3.setSupervisor(administrator);
        employees.save(emp3);

        Employee emp4 = new Employee();
        emp4.setFirstName("Adam");
        emp4.setLastName("Thomas");
        emp4.setEmail("athomas1@gmail.com");
        emp4.setUserName("athomas1");
        emp4.setDepartment(departments.findByName("Supplies").orElseThrow());
        emp4.setPhone("999-999-9999");
        emp4.setSupervisor(administrator);
        employees.save(emp4);

        assertEquals(4, employees.findBySupervisor(administrator).size());

        Schedules schedules = new Schedules();
        schedules.getScheduleList().add(schedule);

        Collection<Assignment> assignments = optimizer.generateSchedules(schedules);
        assertEquals(21, assignments.size());
        assertEquals(0, assignments.stream().filter(a -> a.getEmployee().getUserName().equals("jcarter")).collect(Collectors.toList()).size());
        assertTrue(assignments.stream().filter(a -> a.getEmployee().getUserName().equals("jcarter1")).collect(Collectors.toList()).size() > 0);
    }
}