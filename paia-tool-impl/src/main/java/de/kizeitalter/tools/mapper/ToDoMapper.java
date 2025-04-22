package de.kizeitalter.tools.mapper;

import de.kizeitalter.tools.dto.ToDoDto;
import de.kizeitalter.tools.model.ToDo;
import de.kizeitalter.tools.model.ToDoReminder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ToDoMapper {

    public ToDoDto toDto(ToDo entity) {
        if (entity == null) {
            return null;
        }

        return ToDoDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .userName(entity.getUserName())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .dueDate(getFormat(entity.getDueDate()))
                .reminders(getReminders(entity))
                .build();
    }

    private static List<Integer> getReminders(ToDo entity) {
        if(entity.getReminders() == null) {
            return List.of();
        }
        return entity.getReminders().stream()
                .map(ToDoReminder::getReminderMinutes)
                .collect(Collectors.toList());
    }

    private static String getFormat(ZonedDateTime dueDate) {
        return Optional.ofNullable(dueDate).map(date -> date.format(DateTimeFormatter.ISO_DATE_TIME)).orElse(null);
    }

    public ToDo toEntity(ToDoDto dto) {
        if (dto == null) {
            return null;
        }

        ToDo todo = ToDo.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .userName(dto.getUserName())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .dueDate(convertIsoDateTime(dto.getDueDate()))
                .build();

        if (dto.getReminders() != null) {
            todo.setReminders(dto.getReminders().stream()
                    .map(minutes -> {
                        ToDoReminder reminder = new ToDoReminder();
                        reminder.setTodo(todo);
                        reminder.setReminderMinutes(minutes);
                        return reminder;
                    })
                    .collect(Collectors.toList()));
        }

        return todo;
    }

    private ZonedDateTime convertIsoDateTime(String dueDate) {
        if (dueDate == null) {
            return null;
        }

        return ZonedDateTime.parse(dueDate, DateTimeFormatter.ISO_DATE_TIME);
    }
} 
