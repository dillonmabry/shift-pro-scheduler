package com.projects.shiftproscheduler.assignmentrequest;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface AssignmentRequestRepository extends CrudRepository<AssignmentRequest, Integer> {

    /**
     * Retrieve all <code>Assignment</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Assignment</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("assignmentrequests")
    Collection<AssignmentRequest> findAll() throws DataAccessException;

}
