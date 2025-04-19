package de.kizeitalter.paiatools.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Period;
import java.time.ZonedDateTime;

@Entity
@Table(name = "todo_reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToDoReminder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private ToDo todo;
    
    @Column(nullable = false)
    private Period reminderPeriod;
    
    private ZonedDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }
} 