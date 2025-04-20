package de.kizeitalter.paiatools.dto;

import de.kizeitalter.paiatools.model.ToDoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateDto {
    private ToDoStatus status;
} 