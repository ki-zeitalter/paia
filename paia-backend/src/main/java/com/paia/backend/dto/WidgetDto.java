package com.paia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WidgetDto {
    private String widgetId;
    private String name;
    private String description;
    private List<WidgetConfigParameterDto> configParameters;
} 