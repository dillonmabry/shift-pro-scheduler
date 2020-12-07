package com.projects.shiftproscheduler.department;

import java.util.Optional;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DepartmentRepository extends Repository<Department, Integer> {
    
    /**
     * Save a {@link Department} to the data store, either inserting or updating
     * 
     * @param department the {@link Department} tp save
     */
    void save(Department department);

    /**
     * Find a {@link Department} by name
     * 
     */
    @Transactional(readOnly = true)
    Optional<Department> findByName(@Param("name") String name);

}
