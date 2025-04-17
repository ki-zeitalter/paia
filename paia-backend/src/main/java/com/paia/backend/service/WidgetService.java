package com.paia.backend.service;

import com.paia.backend.dto.WidgetConfigParameterDto;
import com.paia.backend.dto.WidgetDto;
import com.paia.backend.model.AvailableWidget;
import com.paia.backend.model.WidgetConfigParameter;
import com.paia.backend.repository.AvailableWidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WidgetService {

    private final AvailableWidgetRepository availableWidgetRepository;

    @Transactional(readOnly = true)
    public List<WidgetDto> getAllAvailableWidgets() {
        return availableWidgetRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private WidgetDto mapToDto(AvailableWidget widget) {
        List<WidgetConfigParameterDto> configParams = widget.getConfigParameters().stream()
                .map(this::mapConfigParamToDto)
                .collect(Collectors.toList());
                
        return WidgetDto.builder()
                .widgetId(widget.getWidgetId())
                .name(widget.getName())
                .description(widget.getDescription())
                .configParameters(configParams)
                .build();
    }
    
    private WidgetConfigParameterDto mapConfigParamToDto(WidgetConfigParameter param) {
        return WidgetConfigParameterDto.builder()
                .propertyName(param.getPropertyName())
                .propertyDescription(param.getPropertyDescription())
                .required(param.isRequired())
                .build();
    }
} 