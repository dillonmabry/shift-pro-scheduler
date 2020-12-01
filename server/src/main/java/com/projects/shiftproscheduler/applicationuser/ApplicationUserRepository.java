package com.projects.shiftproscheduler.applicationuser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);
}