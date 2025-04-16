package com.paia.backend.repository;

import com.paia.backend.model.LlmModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LlmModelRepository extends JpaRepository<LlmModel, String> {
} 