package de.kizeitalter.paiatools.mapper;

import de.kizeitalter.paiatools.dto.ToDoDto;
import de.kizeitalter.paiatools.model.ToDo;
import de.kizeitalter.paiatools.model.ToDoReminder;
import org.springframework.stereotype.Component;

import java.time.Period;
import java.util.stream.Collectors;

@Component
public class ToDoMapper {

    public ToDoDto toDto(ToDo entity) {
        if (entity == null) {
            return null;
        }
        
        return ToDoDto.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .dueDate(entity.getDueDate())
                .reminders(entity.getReminders().stream()
                        .map(ToDoReminder::getReminderPeriod)
                        .collect(Collectors.toSet()))
                .build();
    }
    
    public ToDo toEntity(ToDoDto dto) {
        if (dto == null) {
            return null;
        }
        
        ToDo todo = ToDo.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .dueDate(dto.getDueDate())
                .build();
        
        if (dto.getReminders() != null) {
            todo.setReminders(dto.getReminders().stream()
                    .map(period -> {
                        ToDoReminder reminder = new ToDoReminder();
                        reminder.setTodo(todo);
                        reminder.setReminderPeriod(period);
                        return reminder;
                    })
                    .collect(Collectors.toSet()));
        }
        
        return todo;
    }
} 