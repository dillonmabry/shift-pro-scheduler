package com.projects.shiftproscheduler.payroll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public DatabaseLoader(EmployeeRepository repository){
        this.employeeRepository = repository;
    }

    @Override
    public void run(String... atrings) throws Exception{
        this.employeeRepository.save(new Employee("Frodo", "Baggins", "ring bearer"));

        Iterator<Employee> itr = this.employeeRepository.findAll().iterator();

        while(itr.hasNext()){
            Employee t = itr.next();
            System.out.println("first name: " + t.getFirstName());

        }
    }

}
