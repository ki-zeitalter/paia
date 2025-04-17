package com.paia.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    
    @OneToMany(mappedBy = "widget", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WidgetConfigParameter> configParameters = new ArrayList<>();
} 