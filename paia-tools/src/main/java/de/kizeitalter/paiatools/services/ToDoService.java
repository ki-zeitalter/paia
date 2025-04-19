package de.kizeitalter.paiatools.services;

import de.kizeitalter.paiatools.dto.ToDoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ToDoService {

    @Tool( description = "Delivers the tasks, to-dos for the user.")
    public List<ToDoDto> getToDos() {
        log.info("getToDos");
        return List.of(
                new ToDoDto("Test 1", "Test 1 Beschreibung", null, null, null, Set.of()),
                new ToDoDto("Test 2", "Test 2 Beschreibung", null, null, null, Set.of())
        );
    }
}
