package com.jobscheduler.dineease.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobscheduler.dineease.entity.AppRole;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

    Optional<AppRole> findByRoleName(String roleName);
}
