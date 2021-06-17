package com.projects.shiftproscheduler.assignmentrequest;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import com.projects.shiftproscheduler.employee.Employee;

public interface AssignmentRequestRepository extends CrudRepository<AssignmentRequest, Integer> {

    /**
     * Retrieve all <code>AssignmentRequest</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>AssignmentRequest</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("assignmentrequests")
    Collection<AssignmentRequest> findAll() throws DataAccessException;

    /**
     * Retrieve all <code>AssignmentRequest</code>s by employee from the data store.
     *
     * @return a <code>Collection</code> of <code>AssignmentRequest</code>s
     */
    @Transactional(readOnly = true)
    Collection<AssignmentRequest> findByEmployee(@Param("employee") Employee employee);

}
