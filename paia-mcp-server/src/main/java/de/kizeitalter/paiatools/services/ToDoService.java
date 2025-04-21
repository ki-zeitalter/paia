package de.kizeitalter.paiatools.services;

import de.kizeitalter.paiatools.dto.ToDoDto;
import de.kizeitalter.paiatools.dto.ToDoPriority;
import de.kizeitalter.paiatools.dto.ToDoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ToDoService {
    private final RestTemplate restTemplate;

    @Value("${paia.tools.api.url}")
    private String paiaToolsApiUrl;

    @Tool(description = "Delivers the tasks (to-dos) for the user.")
    public List<ToDoDto> getToDos(@ToolParam(description = "The username") String userName) {
        log.info("getToDos");
        try {
            String url = paiaToolsApiUrl + "/api/todos?userName=" + userName;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<ToDoDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ToDoDto>>() {
                    }
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der Todos für Benutzer {}: {}", userName, e.getMessage());
            throw new RuntimeException("Fehler beim Abrufen der Todos für Benutzer " + userName + ": " + e.getMessage());
        }
    }


    @Tool(description = "Creates a new task (aka to-do) for the user.")
    public ToDoDto createToDo(@ToolParam(description = "The username") String userName,
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
        try {
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

            String url = paiaToolsApiUrl + "/api/todos";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ToDoDto> entity = new HttpEntity<>(toDoDto, headers);

            return restTemplate.postForObject(url, entity, ToDoDto.class);
        } catch (Exception e) {
            log.error("Fehler beim Erstellen eines Todos: {}", e.getMessage());
            throw new RuntimeException("Fehler beim Erstellen eines Todos: " + e.getMessage());
        }
    }

    @Tool(description = "Sets the status for a task (aka to-do).")
    public ToDoDto setToDoStatus(@ToolParam(description = "The id of the task") Long id,
                                 @ToolParam(description = "Status of the task") ToDoStatus status) {
        log.info("Setting status for ToDo with id: {}", id);
        return null;
    }


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
        try {
            var existingToDo = getTodoById(id);
            if (existingToDo.isPresent()) {
                var toDoDto = ToDoDto
                        .builder()
                        .userName(existingToDo.get().getUserName())
                        .name(Optional.ofNullable(name).orElse(existingToDo.get().getName()))
                        .description(Optional.ofNullable(description).orElse(existingToDo.get().getDescription()))
                        .dueDate(Optional.ofNullable(dueDate).orElse(existingToDo.get().getDueDate()))
                        .priority(Optional.ofNullable(priority).orElse(existingToDo.get().getPriority()))
                        .status(Optional.ofNullable(status).orElse(existingToDo.get().getStatus()))
                        .reminders(Optional.ofNullable(reminders).orElse(existingToDo.get().getReminders()))
                        .build();

                String url = paiaToolsApiUrl + "/api/todos/" + id;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ToDoDto> entity = new HttpEntity<>(toDoDto, headers);

                ResponseEntity<ToDoDto> response = restTemplate.exchange(
                        url,
                        HttpMethod.PUT,
                        entity,
                        ToDoDto.class
                );

                return response.getBody();
            } else {
                log.warn("ToDo mit id: {} nicht gefunden", id);
                throw new RuntimeException("ToDo mit id: " + id + " nicht gefunden");
            }
        } catch (Exception e) {
            log.error("Fehler beim Aktualisieren des Todos mit ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Fehler beim Aktualisieren des Todos: " + e.getMessage());
        }

    }


    public Optional<ToDoDto> getTodoById(Long id) {
        try {
            String url = paiaToolsApiUrl + "/api/todos/" + id;

            return Optional.ofNullable(restTemplate.getForObject(url, ToDoDto.class));
        } catch (Exception e) {
            log.error("Fehler beim Abrufen des Todos mit ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
}
