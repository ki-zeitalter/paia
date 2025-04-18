package de.kizeitalter.paiatools.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToDo {
    private String name;
    private String description;
    private TodDoStatus status;
    private ToDoPriority priority;

    private ZonedDateTime dueDate;

    private Set<Period> reminders;
}
