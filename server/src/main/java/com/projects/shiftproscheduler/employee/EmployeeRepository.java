package com.projects.shiftproscheduler.employee;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import com.projects.shiftproscheduler.administrator.Administrator;

public interface EmployeeRepository extends Repository<Employee, Integer> {

    /**
     * Retrieve all <code>Employee</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Employee</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("employees")
    Collection<Employee> findAll() throws DataAccessException;

    /**
     * Save a {@link Employee} to the data store, either inserting or updating
     * 
     * @param employee the {@link Employee} tp save
     */
    void save(Employee employee);
    
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

    /**
     * Find {@link Employee}s by supervisor username
     * 
     */
    @Query(value = "select e.id, e.first_name, e.last_name, e.email, e.phone, d.id as dept_id, d.dept_name, a.username, ad.username as supervisor, ad.id as supervisor_id from employees e left join application_users a on a.username = e.username join departments d on d.id = e.dept_id join administrators ad on ad.id = e.supervisor_id where ad.username = ?1", nativeQuery = true)
    Collection<Employee> findAllBySupervisor(String username);

}
