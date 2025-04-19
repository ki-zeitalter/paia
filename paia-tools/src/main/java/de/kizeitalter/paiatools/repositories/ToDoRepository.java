package de.kizeitalter.paiatools.repositories;

import de.kizeitalter.paiatools.model.ToDo;
import de.kizeitalter.paiatools.model.ToDoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    
    List<ToDo> findByStatus(ToDoStatus status);
    
    List<ToDo> findByStatusIn(List<ToDoStatus> statusList);
    
    List<ToDo> findByDueDateBefore(ZonedDateTime dateTime);
    
    List<ToDo> findByNameContainingIgnoreCase(String name);
} 