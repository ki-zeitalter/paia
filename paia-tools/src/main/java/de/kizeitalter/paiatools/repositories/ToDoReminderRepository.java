package de.kizeitalter.paiatools.repositories;

import de.kizeitalter.paiatools.model.ToDoReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoReminderRepository extends JpaRepository<ToDoReminder, Long> {
    
    List<ToDoReminder> findByTodoId(Long todoId);
    
    void deleteByTodoId(Long todoId);
} 