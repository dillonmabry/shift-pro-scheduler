package com.projects.shiftproscheduler.employee;

import java.util.Collection;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface EmployeeRepository extends Repository<Employee, Integer> {

    /**
     * Retrieve all <code>Employee</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Employee</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("employees")
    Collection<Employee> findAll() throws DataAccessException;

}
