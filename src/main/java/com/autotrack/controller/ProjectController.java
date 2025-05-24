package com.autotrack.controller;

import com.autotrack.dto.ProjectDTO;
import com.autotrack.model.Project;
import com.autotrack.model.Team;
import com.autotrack.model.User;
import com.autotrack.service.ProjectService;
import com.autotrack.service.TeamService;
import com.autotrack.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for project management.
 */
@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TeamService teamService;
    private final UserService userService;

    @Autowired
    public ProjectController(ProjectService projectService, TeamService teamService, UserService userService) {
        this.projectService = projectService;
        this.teamService = teamService;
        this.userService = userService;
    }

    /**
     * Show all projects.
     */
    @GetMapping
    public String listProjects(@AuthenticationPrincipal OAuth2User principal, Model model) {
        User user = userService.getCurrentUser(principal);
        List<Project> projects = projectService.getProjectsByUser(user);
        model.addAttribute("projects", projects);
        return "project/list";
    }

    /**
     * Show project creation form.
     */
    @GetMapping("/create")
    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    public String showCreateForm(Model model, @AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        List<Team> teams = teamService.getTeamsByUser(user);
        
        model.addAttribute("projectDTO", new ProjectDTO());
        model.addAttribute("teams", teams);
        return "project/create";
    }

    /**
     * Process project creation.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    public String createProject(@Valid @ModelAttribute("projectDTO") ProjectDTO projectDTO,
                                BindingResult result,
                                @AuthenticationPrincipal OAuth2User principal,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        if (result.hasErrors()) {
            User user = userService.getCurrentUser(principal);
            List<Team> teams = teamService.getTeamsByUser(user);
            model.addAttribute("teams", teams);
            return "project/create";
        }
        
        try {
            Project project = projectService.createProject(projectDTO);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Project " + project.getName() + " created successfully!");
            return "redirect:/projects/" + project.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating project: " + e.getMessage());
            User user = userService.getCurrentUser(principal);
            List<Team> teams = teamService.getTeamsByUser(user);
            model.addAttribute("teams", teams);
            return "project/create";
        }
    }

    /**
     * Show project details.
     */
    @GetMapping("/{id}")
    public String showProject(@PathVariable Long id, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        return "project/detail";
    }

    /**
     * Show project edit form.
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setGitHubRepoUrl(project.getGitHubRepoUrl());
        projectDTO.setTeamId(project.getTeam().getId());
        
        model.addAttribute("projectDTO", projectDTO);
        model.addAttribute("teams", teamService.getAllTeams());
        return "project/edit";
    }

    /**
     * Process project update.
     */
    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    public String updateProject(@PathVariable Long id,
                               @Valid @ModelAttribute("projectDTO") ProjectDTO projectDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("teams", teamService.getAllTeams());
            return "project/edit";
        }
        
        try {
            Project project = projectService.updateProject(id, projectDTO);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Project " + project.getName() + " updated successfully!");
            return "redirect:/projects/" + project.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating project: " + e.getMessage());
            model.addAttribute("teams", teamService.getAllTeams());
            return "project/edit";
        }
    }

    /**
     * Delete project.
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Project project = projectService.getProjectById(id);
            projectService.deleteProject(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Project " + project.getName() + " deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting project: " + e.getMessage());
        }
        return "redirect:/projects";
    }
}
