package com.projects.shiftproscheduler.employee;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import com.projects.shiftproscheduler.administrator.Administrator;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

    /**
     * Retrieve all <code>Employee</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Employee</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("employees")
    Collection<Employee> findAll() throws DataAccessException;

    /**
     * Find a {@link Employee} by id
     * 
     */
    @Transactional(readOnly = true)
    Optional<Employee> findById(Integer id);

    /**
     * Find a {@link Employee} by username
     * 
     */
    @Transactional(readOnly = true)
    Optional<Employee> findByUserName(@Param("username") String username);

    /**
     * Find {@link Employee}s by supervisor
     * 
     */
    @Transactional(readOnly = true)
    Collection<Employee> findBySupervisor(Administrator supervisor);

}
