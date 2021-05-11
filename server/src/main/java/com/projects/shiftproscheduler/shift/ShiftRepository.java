package com.projects.shiftproscheduler.shift;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

public interface ShiftRepository extends CrudRepository<Shift, Integer> {

    /**
     * Retrieve all <code>Shift</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Shift</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("shifts")
    Collection<Shift> findAll() throws DataAccessException;

    /**
     * Find a {@link Schedule} by id
     * 
     */
    @Transactional(readOnly = true)
    Optional<Shift> findById(Integer id);

}
