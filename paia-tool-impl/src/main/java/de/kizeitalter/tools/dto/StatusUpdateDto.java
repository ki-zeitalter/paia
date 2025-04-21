package de.kizeitalter.tools.dto;


import de.kizeitalter.tools.model.ToDoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateDto {
    private ToDoStatus status;
} 
