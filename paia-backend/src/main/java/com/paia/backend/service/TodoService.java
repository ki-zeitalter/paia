package com.paia.backend.service;

import com.paia.backend.dto.StatusUpdateDto;
import com.paia.backend.dto.TodoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final RestTemplate restTemplate;

    @Value("${paia.tools.api.url}")
    private String paiaToolsApiUrl;

    public List<TodoDto> getTodosForUser(String userName) {
        try {
            String url = paiaToolsApiUrl + "/api/todos?userName=" + userName;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List<TodoDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<TodoDto>>() {}
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der Todos für Benutzer {}: {}", userName, e.getMessage());
            return Collections.emptyList();
        }
    }

    public TodoDto getTodoById(Long id) {
        try {
            String url = paiaToolsApiUrl + "/api/todos/" + id;
            
            return restTemplate.getForObject(url, TodoDto.class);
        } catch (Exception e) {
            log.error("Fehler beim Abrufen des Todos mit ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public TodoDto createTodo(TodoDto todoDto) {
        try {
            String url = paiaToolsApiUrl + "/api/todos";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TodoDto> entity = new HttpEntity<>(todoDto, headers);
            
            return restTemplate.postForObject(url, entity, TodoDto.class);
        } catch (Exception e) {
            log.error("Fehler beim Erstellen eines Todos: {}", e.getMessage());
            return null;
        }
    }

    public TodoDto updateTodo(Long id, TodoDto todoDto) {
        try {
            String url = paiaToolsApiUrl + "/api/todos/" + id;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TodoDto> entity = new HttpEntity<>(todoDto, headers);
            
            ResponseEntity<TodoDto> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                TodoDto.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Fehler beim Aktualisieren des Todos mit ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public TodoDto updateTodoStatus(Long id, StatusUpdateDto statusUpdate) {
        try {
            String url = paiaToolsApiUrl + "/api/todos/" + id + "/status";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<StatusUpdateDto> entity = new HttpEntity<>(statusUpdate, headers);
            
            ResponseEntity<TodoDto> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                entity,
                TodoDto.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Fehler beim Aktualisieren des Status für Todo mit ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteTodo(Long id) {
        try {
            String url = paiaToolsApiUrl + "/api/todos/" + id;
            restTemplate.delete(url);
        } catch (Exception e) {
            log.error("Fehler beim Löschen des Todos mit ID {}: {}", id, e.getMessage());
        }
    }
} 
