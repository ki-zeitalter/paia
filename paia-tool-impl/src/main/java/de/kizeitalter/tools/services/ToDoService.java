package de.kizeitalter.tools.services;

import de.kizeitalter.tools.dto.ToDoDto;
import de.kizeitalter.tools.mapper.ToDoMapper;
import de.kizeitalter.tools.model.ToDo;
import de.kizeitalter.tools.model.ToDoPriority;
import de.kizeitalter.tools.model.ToDoReminder;
import de.kizeitalter.tools.model.ToDoStatus;
import de.kizeitalter.tools.repositories.ToDoReminderRepository;
import de.kizeitalter.tools.repositories.ToDoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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


    @Transactional(readOnly = true)
    public List<ToDoDto> getToDos(String userName) {
        log.info("getToDos");
        List<ToDo> todos = toDoRepository.findByUserNameAndStatusIn(userName,
                List.of(ToDoStatus.OPEN, ToDoStatus.IN_PROGRESS, ToDoStatus.DRAFT)
        );
        return todos.stream()
                .map(toDoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ToDoDto createToDo( String userName,

                              String name,

                              String description,

                              String dueDate,

                                   ToDoPriority priority,

                              List<Integer> reminders) {
        var toDoDto = ToDoDto
                .builder()
                .userName(userName)
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


    public ToDoDto setToDoStatus( Long id,
                                  ToDoStatus status) {
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

    public ToDoDto updateToDo(

            Long id,

            String name,

            String description,

            String dueDate,

            ToDoPriority priority,

            List<Integer> reminders,

            ToDoStatus status) {


        log.info("Updating ToDo with id: {}", id);
        Optional<ToDo> existingToDo = toDoRepository.findById(id);

        if (existingToDo.isPresent()) {
            var toDoDto = ToDoDto
                    .builder()
                    .userName(existingToDo.get().getUserName())
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
