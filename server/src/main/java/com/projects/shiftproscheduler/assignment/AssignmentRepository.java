package com.projects.shiftproscheduler.assignment;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import com.projects.shiftproscheduler.employee.Employee;

public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {

    /**
     * Retrieve all <code>Assignment</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Assignment</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("assignments")
    Collection<Assignment> findAll() throws DataAccessException;

    @Transactional
    void deleteAllByEmployee(Employee employee);

}
