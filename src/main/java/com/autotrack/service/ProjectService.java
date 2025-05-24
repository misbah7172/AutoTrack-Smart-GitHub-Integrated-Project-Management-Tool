package com.autotrack.service;

import com.autotrack.dto.ProjectDTO;
import com.autotrack.model.Project;
import com.autotrack.model.Team;
import com.autotrack.model.User;
import com.autotrack.repository.ProjectRepository;
import com.autotrack.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing projects.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, TeamRepository teamRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Get all projects.
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Get projects for a user.
     */
    public List<Project> getProjectsByUser(User user) {
        return projectRepository.findProjectsByUserId(user.getId());
    }

    /**
     * Get projects for a team.
     */
    public List<Project> getProjectsByTeam(Team team) {
        return projectRepository.findByTeam(team);
    }

    /**
     * Get a project by ID.
     */
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    /**
     * Create a new project.
     */
    @Transactional
    public Project createProject(ProjectDTO projectDTO) {
        Team team = teamRepository.findById(projectDTO.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + projectDTO.getTeamId()));
        
        // Generate a webhook secret
        String webhookSecret = UUID.randomUUID().toString();
        
        Project project = Project.builder()
                .name(projectDTO.getName())
                .gitHubRepoUrl(projectDTO.getGitHubRepoUrl())
                .gitHubRepoId(projectDTO.getGitHubRepoId())
                .gitHubAccessToken(projectDTO.getGitHubAccessToken())
                .webhookSecret(webhookSecret)
                .team(team)
                .build();
        
        return projectRepository.save(project);
    }

    /**
     * Update an existing project.
     */
    @Transactional
    public Project updateProject(Long id, ProjectDTO projectDTO) {
        Project existingProject = getProjectById(id);
        
        Team team = teamRepository.findById(projectDTO.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + projectDTO.getTeamId()));
        
        existingProject.setName(projectDTO.getName());
        existingProject.setGitHubRepoUrl(projectDTO.getGitHubRepoUrl());
        
        if (projectDTO.getGitHubRepoId() != null) {
            existingProject.setGitHubRepoId(projectDTO.getGitHubRepoId());
        }
        
        if (projectDTO.getGitHubAccessToken() != null && !projectDTO.getGitHubAccessToken().isEmpty()) {
            existingProject.setGitHubAccessToken(projectDTO.getGitHubAccessToken());
        }
        
        existingProject.setTeam(team);
        
        return projectRepository.save(existingProject);
    }

    /**
     * Delete a project.
     */
    @Transactional
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
}
