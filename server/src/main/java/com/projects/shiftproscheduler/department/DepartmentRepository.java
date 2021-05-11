package com.projects.shiftproscheduler.department;

import java.util.Collection;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DepartmentRepository extends CrudRepository<Department, Integer> {

    /**
     * Retrieve all <code>Department</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Department</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("shifts")
    Collection<Department> findAll() throws DataAccessException;

    /**
     * Find a {@link Department} by name
     * 
     */
    @Transactional(readOnly = true)
    Optional<Department> findByName(@Param("name") String name);

}
