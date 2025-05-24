package com.autotrack.dto;

import com.autotrack.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for Task.
 */
@Data
public class TaskDTO {
    
    private Long id;
    
    @NotBlank(message = "Feature code is required")
    private String featureCode;
    
    @NotBlank(message = "Task title is required")
    private String title;
    
    @NotNull(message = "Status is required")
    private TaskStatus status;
    
    private Long assigneeId;
    
    @NotNull(message = "Project is required")
    private Long projectId;
    
    private String milestone;
    
    private String tags;
}
