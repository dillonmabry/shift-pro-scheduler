package com.projects.shiftproscheduler.administrator;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface AdministratorRepository extends Repository<Administrator, Integer> {

    /**
     * Retrieve all <code>Administrator</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Administrator</code>s
     */
    @Transactional(readOnly = true)
    @Cacheable("administrators")
    Collection<Administrator> findAll() throws DataAccessException;

}
