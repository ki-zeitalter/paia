package de.kizeitalter.tools.repositories;

import de.kizeitalter.tools.model.ToDo;
import de.kizeitalter.tools.model.ToDoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    
    List<ToDo> findByStatus(ToDoStatus status);
    
    List<ToDo> findByUserNameAndStatusIn(String userName, List<ToDoStatus> statusList);
    
    List<ToDo> findByDueDateBefore(ZonedDateTime dateTime);
    
    List<ToDo> findByNameContainingIgnoreCase(String name);
} 
