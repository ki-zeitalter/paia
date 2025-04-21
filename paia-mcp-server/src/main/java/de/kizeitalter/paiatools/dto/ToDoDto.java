package de.kizeitalter.paiatools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToDoDto {
    private Long id;
    private String name;
    private String description;
    private String userName;
    private ToDoStatus status;
    private ToDoPriority priority;

    private String dueDate;

    private List<Integer> reminders;
}
