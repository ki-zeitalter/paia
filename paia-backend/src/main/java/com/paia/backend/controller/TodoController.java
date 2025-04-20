package com.paia.backend.controller;

import com.paia.backend.dto.TodoDto;
import com.paia.backend.dto.StatusUpdateDto;
import com.paia.backend.model.User;
import com.paia.backend.service.TodoService;
import com.paia.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<TodoDto>> getTodos(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<TodoDto> todos = todoService.getTodosForUser(user.getEmail());
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodoById(@PathVariable Long id) {
        TodoDto todo = todoService.getTodoById(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(todo);
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody TodoDto todoDto, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        todoDto.setUserName(user.getEmail());
        TodoDto createdTodo = todoService.createTodo(todoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(@PathVariable Long id, @RequestBody TodoDto todoDto, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        todoDto.setId(id);
        todoDto.setUserName(user.getEmail());
        
        TodoDto updatedTodo = todoService.updateTodo(id, todoDto);
        if (updatedTodo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTodo);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TodoDto> updateTodoStatus(@PathVariable Long id, @RequestBody StatusUpdateDto statusUpdate) {
        TodoDto updatedTodo = todoService.updateTodoStatus(id, statusUpdate);
        if (updatedTodo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    private User getUserFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return userService.getOrCreateUserFromJwt(jwt);
    }
} 