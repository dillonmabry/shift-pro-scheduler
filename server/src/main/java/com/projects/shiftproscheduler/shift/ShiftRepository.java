package com.projects.shiftproscheduler.shift;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface ShiftRepository extends Repository<Shift, Integer> {

    /**
     * Retrieve all <code>Shift</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Shift</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("shifts")
    Collection<Shift> findAll() throws DataAccessException;

}
