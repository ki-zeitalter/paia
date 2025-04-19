package de.kizeitalter.paiatools.services;

import de.kizeitalter.paiatools.dto.ToDoDto;
import de.kizeitalter.paiatools.mapper.ToDoMapper;
import de.kizeitalter.paiatools.model.ToDo;
import de.kizeitalter.paiatools.model.ToDoStatus;
import de.kizeitalter.paiatools.repositories.ToDoRepository;
import de.kizeitalter.paiatools.repositories.ToDoReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ToDoDto createToDo(ToDoDto toDoDto) {
        log.info("Creating ToDo: {}", toDoDto.getName());
        ToDo todo = toDoMapper.toEntity(toDoDto);
        ToDo savedToDo = toDoRepository.save(todo);
        return toDoMapper.toDto(savedToDo);
    }
    
    @Transactional
    public ToDoDto updateToDo(Long id, ToDoDto toDoDto) {
        log.info("Updating ToDo with id: {}", id);
        Optional<ToDo> existingToDo = toDoRepository.findById(id);
        
        if (existingToDo.isPresent()) {
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
