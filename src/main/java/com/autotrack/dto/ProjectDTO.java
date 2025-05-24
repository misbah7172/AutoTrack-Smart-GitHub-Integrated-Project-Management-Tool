package com.autotrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for Project.
 */
@Data
public class ProjectDTO {
    
    private Long id;
    
    @NotBlank(message = "Project name is required")
    private String name;
    
    @NotBlank(message = "GitHub repository URL is required")
    private String gitHubRepoUrl;
    
    private String gitHubRepoId;
    
    private String gitHubAccessToken;
    
    @NotNull(message = "Team is required")
    private Long teamId;
}
