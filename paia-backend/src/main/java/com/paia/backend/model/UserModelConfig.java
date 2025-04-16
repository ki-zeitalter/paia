package com.paia.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_model_configs", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "model_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModelConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "model_id", nullable = false)
    private String modelId;

    @Column(name = "api_key")
    private String apiKey;

    // weitere Konfigurationsparameter können hier hinzugefügt werden

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 