package com.projects.shiftproscheduler.schedule;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

public interface ScheduleRepository extends CrudRepository<Schedule, Integer> {

    /**
     * Retrieve all <code>Schedule</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Schedule</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("schedules")
    Collection<Schedule> findAll() throws DataAccessException;

    /**
     * Find a {@link Schedule} by id
     * 
     */
    @Transactional(readOnly = true)
    Optional<Schedule> findById(Integer id);

    /**
     * Retrieve all <code>Schedule</code>s by active status
     *
     * @return a Optional <code>Collection</code> of <code>Schedule</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("schedules")
    Optional<Collection<Schedule>> findAllByIsActive(boolean isActive) throws DataAccessException;

}
