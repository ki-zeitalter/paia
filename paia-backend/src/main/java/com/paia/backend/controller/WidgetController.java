package com.paia.backend.controller;

import com.paia.backend.dto.WidgetDto;
import com.paia.backend.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/widgets")
@RequiredArgsConstructor
public class WidgetController {

    private final WidgetService widgetService;

    @GetMapping("/available")
    public ResponseEntity<List<WidgetDto>> getAvailableWidgets() {
        return ResponseEntity.ok(widgetService.getAllAvailableWidgets());
    }
} 