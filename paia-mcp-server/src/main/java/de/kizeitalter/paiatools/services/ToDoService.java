package de.kizeitalter.paiatools.services;

import de.kizeitalter.paiatools.dto.ToDoDto;
import de.kizeitalter.paiatools.dto.ToDoPriority;
import de.kizeitalter.paiatools.dto.ToDoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ToDoService {


    @Tool(description = "Delivers the tasks (to-dos) for the user.")
    public List<ToDoDto> getToDos(@ToolParam(description = "The username") String userName) {
        log.info("getToDos");
        return null;
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
        return null;
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


        return null;
    }


}
