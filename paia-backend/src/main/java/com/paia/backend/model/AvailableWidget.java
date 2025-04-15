package com.paia.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "available_widgets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableWidget {

    @Id
    @Column(name = "widget_id")
    private String widgetId;

    @Column(nullable = false)
    private String name;

    private String description;
} 