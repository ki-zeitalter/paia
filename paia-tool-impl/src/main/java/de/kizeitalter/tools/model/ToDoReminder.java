package de.kizeitalter.tools.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer reminderMinutes;
    
    private ZonedDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }
} 
