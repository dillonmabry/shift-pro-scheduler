package com.projects.shiftproscheduler.security;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends Repository<Role, Integer> {
    /**
     * Find a {@link Role} by name
     * 
     */
    @Transactional()
    Optional<Role> findByName(@Param("name") String name);

}
