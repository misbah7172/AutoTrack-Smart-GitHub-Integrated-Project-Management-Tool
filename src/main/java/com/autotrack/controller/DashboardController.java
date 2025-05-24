package com.autotrack.controller;

import com.autotrack.model.Project;
import com.autotrack.model.Team;
import com.autotrack.model.User;
import com.autotrack.service.ProjectService;
import com.autotrack.service.TeamService;
import com.autotrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller for dashboard and home page.
 */
@Controller
public class DashboardController {

    private final UserService userService;
    private final TeamService teamService;
    private final ProjectService projectService;

    @Autowired
    public DashboardController(UserService userService, TeamService teamService, ProjectService projectService) {
        this.userService = userService;
        this.teamService = teamService;
        this.projectService = projectService;
    }

    /**
     * Main dashboard endpoint.
     * Shows teams and projects for the authenticated user.
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        User user = userService.getCurrentUser(principal);
        List<Team> teams = teamService.getTeamsByUser(user);
        List<Project> projects = projectService.getProjectsByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("teams", teams);
        model.addAttribute("projects", projects);
        
        return "dashboard";
    }
}
