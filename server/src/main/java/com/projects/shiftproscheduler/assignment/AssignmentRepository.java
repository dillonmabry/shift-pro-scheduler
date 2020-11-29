package com.projects.shiftproscheduler.assignment;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface AssignmentRepository extends Repository<Assignment, Integer> {

    /**
     * Retrieve all <code>Assignment</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Assignment</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("assignments")
    Collection<Assignment> findAll() throws DataAccessException;

}
