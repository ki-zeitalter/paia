package com.paia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoDto {
    private Long id;
    private String name;
    private String description;
    private String userName;
    private String status;
    private String priority;
    private String dueDate;
    private List<Integer> reminders;
} 