package com.jobscheduler.dineease.repository;

import com.jobscheduler.dineease.entity.CustomerSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerSessionRepository
        extends JpaRepository<CustomerSession, Long> {

    Optional<CustomerSession> findByTokenHash(String tokenHash);

    void deleteByTokenHash(String tokenHash);
}
