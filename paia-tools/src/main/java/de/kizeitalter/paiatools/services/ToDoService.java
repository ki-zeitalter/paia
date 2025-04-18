package de.kizeitalter.paiatools.services;

import de.kizeitalter.paiatools.model.ToDo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ToDoService {

    @Tool(name="Get ToDos", description = "Get all ToDos")
    public List<ToDo> getToDos() {
        return List.of(
                new ToDo("Test 1", "Test 1 Beschreibung", null, null, null, Set.of()),
                new ToDo("Test 2", "Test 2 Beschreibung", null, null, null, Set.of())
        );
    }
}
