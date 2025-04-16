package com.paia.backend.repository;

import com.paia.backend.model.UserModelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserModelConfigRepository extends JpaRepository<UserModelConfig, Long> {
    List<UserModelConfig> findByUserId(String userId);
    Optional<UserModelConfig> findByUserIdAndModelId(String userId, String modelId);
} 