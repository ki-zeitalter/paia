package com.paia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModelConfigDto {
    private String modelId;
    private String apiKey;
    // Weitere Konfigurationsparameter können hier hinzugefügt werden
} 