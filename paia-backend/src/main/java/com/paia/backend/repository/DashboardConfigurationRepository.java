package com.paia.backend.repository;

import com.paia.backend.model.DashboardConfiguration;
import com.paia.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DashboardConfigurationRepository extends JpaRepository<DashboardConfiguration, Long> {
    Optional<DashboardConfiguration> findByUser(User user);
    Optional<DashboardConfiguration> findByUserId(String userId);
} 