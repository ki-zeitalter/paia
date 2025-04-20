package de.kizeitalter.paiatools.services;

import de.kizeitalter.paiatools.dto.ToDoDto;
import de.kizeitalter.paiatools.mapper.ToDoMapper;
import de.kizeitalter.paiatools.model.ToDo;
import de.kizeitalter.paiatools.model.ToDoPriority;
import de.kizeitalter.paiatools.model.ToDoReminder;
import de.kizeitalter.paiatools.model.ToDoStatus;
import de.kizeitalter.paiatools.repositories.ToDoRepository;
import de.kizeitalter.paiatools.repositories.ToDoReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final ToDoReminderRepository reminderRepository;
    private final ToDoMapper toDoMapper;

    @Tool(description = "Delivers the tasks (to-dos) for the user.")
    @Transactional(readOnly = true)
    public List<ToDoDto> getToDos() {
        log.info("getToDos");
        List<ToDo> todos = toDoRepository.findByStatusIn(
                List.of(ToDoStatus.OPEN, ToDoStatus.IN_PROGRESS, ToDoStatus.DRAFT)
        );
        return todos.stream()
                .map(toDoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Tool(description = "Creates a new task (aka to-do) for the user.")
    public ToDoDto createToDo(
            @ToolParam(description = "Name of the task")
            String name,
            @ToolParam(description = "Description of the task. Use Markdown syntax for formatting.")
            String description,
            @ToolParam(description = "Due date with time in ISO 8601 format", required = false)
            String dueDate,
            @ToolParam(description = "Priority of the task", required = false)
            ToDoPriority priority,
            @ToolParam(description = "List of reminders in minutes", required = false)
            List<Integer> reminders) {
        var toDoDto = ToDoDto
                .builder()
                .name(name)
                .description(description)
                .dueDate(dueDate)
                .priority(priority)
                .status(ToDoStatus.OPEN)
                .reminders(reminders)
                .build();
        log.info("Creating ToDo: {}", toDoDto.getName());

        ToDo todo = toDoMapper.toEntity(toDoDto);
        ToDo savedToDo = toDoRepository.save(todo);
        return toDoMapper.toDto(savedToDo);
    }

    @Tool(description = "Sets the status for a task (aka to-do).")
    public ToDoDto setToDoStatus(@ToolParam(description = "The id of the task") Long id,
                                 @ToolParam(description = "Status of the task") ToDoStatus status) {
        log.info("Setting status for ToDo with id: {}", id);
        Optional<ToDo> existingToDo = toDoRepository.findById(id);

        if (existingToDo.isPresent()) {
            var toBeUpdated = existingToDo.get();
            toBeUpdated.setStatus(status);

            ToDo savedToDo = toDoRepository.save(toBeUpdated);
            return toDoMapper.toDto(savedToDo);
        } else {
            log.warn("ToDo with id: {} not found", id);
            return null;
        }
    }

    @Transactional
    @Tool(description = "Update a task (aka to-do) for the user.")
    public ToDoDto updateToDo(
            @ToolParam(description = "The id of the task")
            Long id,
            @ToolParam(description = "Name of the task", required = false)
            String name,
            @ToolParam(description = "Description of the task. Use Markdown syntax for formatting.", required = false)
            String description,
            @ToolParam(description = "Due date-time in ISO 8601 format", required = false)
            String dueDate,
            @ToolParam(description = "Priority of the task", required = false)
            ToDoPriority priority,
            @ToolParam(description = "List of reminders in minutes", required = false)
            List<Integer> reminders,
            @ToolParam(description = "Status of the task", required = false)
            ToDoStatus status) {


        log.info("Updating ToDo with id: {}", id);
        Optional<ToDo> existingToDo = toDoRepository.findById(id);

        if (existingToDo.isPresent()) {
            var toDoDto = ToDoDto
                    .builder()
                    .name(Optional.ofNullable(name).orElse(existingToDo.get().getName()))
                    .description(Optional.ofNullable(description).orElse(existingToDo.get().getDescription()))
                    .dueDate(Optional.ofNullable(dueDate).orElse(getFormat(existingToDo.get().getDueDate())))
                    .priority(Optional.ofNullable(priority).orElse(existingToDo.get().getPriority()))
                    .status(Optional.ofNullable(status).orElse(existingToDo.get().getStatus()))
                    .reminders(Optional.ofNullable(reminders).orElse(convertReminders(existingToDo.get().getReminders())))
                    .build();

            ToDo updatedToDo = toDoMapper.toEntity(toDoDto);
            updatedToDo.setId(id);
            updatedToDo.setCreatedAt(existingToDo.get().getCreatedAt());

            // Löschen der alten Reminders und Ersetzen mit neuen
            reminderRepository.deleteByTodoId(id);

            ToDo savedToDo = toDoRepository.save(updatedToDo);
            return toDoMapper.toDto(savedToDo);
        } else {
            log.warn("ToDo with id: {} not found", id);
            return null;
        }
    }

    private static String getFormat(ZonedDateTime dueDate) {
        return Optional.ofNullable(dueDate).map(date -> date.format(DateTimeFormatter.ISO_DATE_TIME)).orElse(null);
    }

    private List<Integer> convertReminders(List<ToDoReminder> reminders) {
        if (reminders == null) {
            return List.of();
        }
        return reminders.stream()
                .map(ToDoReminder::getReminderMinutes)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteToDo(Long id) {
        log.info("Deleting ToDo with id: {}", id);
        toDoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ToDoDto getToDo(Long id) {
        log.info("Getting ToDo with id: {}", id);
        return toDoRepository.findById(id)
                .map(toDoMapper::toDto)
                .orElse(null);
    }
}
