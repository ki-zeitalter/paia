package com.paia.backend.repository;

import com.paia.backend.model.AvailableWidget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableWidgetRepository extends JpaRepository<AvailableWidget, String> {
} 