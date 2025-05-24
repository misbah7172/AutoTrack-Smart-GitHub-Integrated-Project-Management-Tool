package com.autotrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for Team.
 */
@Data
public class TeamDTO {
    
    private Long id;
    
    @NotBlank(message = "Team name is required")
    private String name;
    
    @NotEmpty(message = "Team must have at least one member")
    private List<Long> memberIds;
}
