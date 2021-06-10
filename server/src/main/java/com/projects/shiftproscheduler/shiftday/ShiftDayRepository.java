package com.projects.shiftproscheduler.shiftday;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface ShiftDayRepository extends CrudRepository<ShiftDay, Integer> {

    /**
     * Retrieve all <code>ShiftDay</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>ShiftDay</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("shifts")
    Collection<ShiftDay> findAll() throws DataAccessException;

}
