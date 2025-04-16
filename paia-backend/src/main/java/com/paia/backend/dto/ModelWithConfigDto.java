package com.paia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelWithConfigDto {
    private String id;
    private String name;
    private String iconUrl;
    private boolean configured;
    // Die API-Keys werden aus Sicherheitsgründen nicht direkt zurückgegeben
} 