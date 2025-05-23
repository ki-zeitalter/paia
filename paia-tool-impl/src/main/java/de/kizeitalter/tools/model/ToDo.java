package de.kizeitalter.tools.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToDo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column
    private String userName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ToDoStatus status;
    
    @Enumerated(EnumType.STRING)
    private ToDoPriority priority;
    
    private ZonedDateTime dueDate;
    
    private ZonedDateTime createdAt;
    
    private ZonedDateTime updatedAt;
    
    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ToDoReminder> reminders = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
        if (status == null) {
            status = ToDoStatus.OPEN;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
} 
