package de.kizeitalter.paiatools.controller;

import de.kizeitalter.paiatools.dto.StatusUpdateDto;
import de.kizeitalter.paiatools.dto.ToDoDto;
import de.kizeitalter.paiatools.model.ToDoPriority;
import de.kizeitalter.paiatools.model.ToDoStatus;
import de.kizeitalter.paiatools.services.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Slf4j
public class ToDoController {

    private final ToDoService toDoService;

    @GetMapping
    public ResponseEntity<List<ToDoDto>> getAllTodos(@RequestParam(required = false) String userName) {
        if (userName == null || userName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<ToDoDto> todos = toDoService.getToDos(userName);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDoDto> getTodoById(@PathVariable Long id) {
        ToDoDto todo = toDoService.getToDo(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(todo);
    }

    @PostMapping
    public ResponseEntity<ToDoDto> createTodo(@RequestBody ToDoDto todoDto) {
        log.info("Erstelle neues ToDo: {}", todoDto.getName());
        ToDoDto createdTodo = toDoService.createToDo(
                todoDto.getUserName(), 
                todoDto.getName(), 
                todoDto.getDescription(), 
                todoDto.getDueDate(), 
                todoDto.getPriority(), 
                todoDto.getReminders()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDoDto> updateTodo(@PathVariable Long id, @RequestBody ToDoDto todoDto) {
        log.info("Aktualisiere ToDo mit ID: {}", id);
        todoDto.setId(id); // Stelle sicher, dass die ID gesetzt ist
        
        ToDoDto updatedTodo = toDoService.updateToDo(
                id,
                todoDto.getName(),
                todoDto.getDescription(),
                todoDto.getDueDate(),
                todoDto.getPriority(),
                todoDto.getReminders(),
                todoDto.getStatus()
        );
        
        if (updatedTodo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTodo);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ToDoDto> updateTodoStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDto statusUpdate) {
        
        log.info("Aktualisiere Status für ToDo mit ID: {} auf {}", id, statusUpdate.getStatus());
        ToDoDto updatedTodo = toDoService.setToDoStatus(id, statusUpdate.getStatus());
        if (updatedTodo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        log.info("Lösche ToDo mit ID: {}", id);
        toDoService.deleteToDo(id);
        return ResponseEntity.noContent().build();
    }
} 