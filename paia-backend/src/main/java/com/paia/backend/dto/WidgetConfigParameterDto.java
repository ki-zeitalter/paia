package com.paia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WidgetConfigParameterDto {
    private String propertyName;
    private String propertyDescription;
    private boolean required;
} 