package de.kizeitalter.paiatools.dto;

import de.kizeitalter.paiatools.model.ToDoPriority;
import de.kizeitalter.paiatools.model.ToDoStatus;
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
public class ToDoDto {
    private String name;
    private String description;
    private ToDoStatus status;
    private ToDoPriority priority;

    private String dueDate;

    //private Set<Period> reminders;
}
