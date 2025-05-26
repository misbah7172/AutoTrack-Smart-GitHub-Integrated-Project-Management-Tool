package com.autotrack.controller;

import com.autotrack.dto.TeamDTO;
import com.autotrack.model.Team;
import com.autotrack.model.User;
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
 * Controller for team management.
 */
@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    @Autowired
    public TeamController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    /**
     * Show all teams.
     */
    @GetMapping
    public String listTeams(@AuthenticationPrincipal OAuth2User principal, Model model) {
        User user = userService.getCurrentUser(principal);
        List<Team> teams = teamService.getTeamsByUser(user);
        model.addAttribute("teams", teams);
        return "team/list";
    }

    /**
     * Show team creation form.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("teamDTO", new TeamDTO());
        model.addAttribute("allUsers", userService.getAllUsers());
        return "team/create";
    }

    /**
     * Process team creation.
     */
    @PostMapping("/create")
    public String createTeam(@Valid @ModelAttribute("teamDTO") TeamDTO teamDTO,
                            BindingResult result,
                            @AuthenticationPrincipal OAuth2User principal,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("allUsers", userService.getAllUsers());
            return "team/create";
        }
        
        try {
            User currentUser = userService.getCurrentUser(principal);
            Team team = teamService.createTeam(teamDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Team " + team.getName() + " created successfully!");
            return "redirect:/teams/" + team.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating team: " + e.getMessage());
            model.addAttribute("allUsers", userService.getAllUsers());
            return "team/create";
        }
    }

    /**
     * Show team details.
     */
    @GetMapping("/{id}")
    public String showTeam(@PathVariable Long id, Model model) {
        Team team = teamService.getTeamById(id);
        model.addAttribute("team", team);
        return "team/detail";
    }

    /**
     * Show team edit form.
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Team team = teamService.getTeamById(id);
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        
        List<Long> memberIds = team.getMembers().stream()
                .map(User::getId)
                .toList();
        teamDTO.setMemberIds(memberIds);
        
        model.addAttribute("teamDTO", teamDTO);
        model.addAttribute("allUsers", userService.getAllUsers());
        return "team/edit";
    }

    /**
     * Process team update.
     */
    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    public String updateTeam(@PathVariable Long id,
                            @Valid @ModelAttribute("teamDTO") TeamDTO teamDTO,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("allUsers", userService.getAllUsers());
            return "team/edit";
        }
        
        try {
            Team team = teamService.updateTeam(id, teamDTO);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Team " + team.getName() + " updated successfully!");
            return "redirect:/teams/" + team.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating team: " + e.getMessage());
            model.addAttribute("allUsers", userService.getAllUsers());
            return "team/edit";
        }
    }

    /**
     * Delete team.
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    public String deleteTeam(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Team team = teamService.getTeamById(id);
            teamService.deleteTeam(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Team " + team.getName() + " deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting team: " + e.getMessage());
        }
        return "redirect:/teams";
    }

    /**
     * Show team members management page.
     */
    @GetMapping("/{id}/members")
    public String showMembers(@PathVariable Long id, Model model) {
        Team team = teamService.getTeamById(id);
        model.addAttribute("team", team);
        return "team/members";
    }

    /**
     * Add member to team by GitHub username.
     */
    @PostMapping("/{id}/members/add")
    public String addMember(@PathVariable Long id, 
                           @RequestParam String githubUsername,
                           @RequestParam String commitUsername,
                           @RequestParam(required = false) String userEmail,
                           @RequestParam(required = false) boolean isTeamLead,
                           RedirectAttributes redirectAttributes) {
        try {
            Team team = teamService.getTeamById(id);
            User newMember = userService.findOrCreateUserByGitHub(githubUsername, commitUsername, userEmail, isTeamLead);
            teamService.addMemberToTeam(team, newMember);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Member " + commitUsername + " added successfully!");
            return "redirect:/teams/" + id + "/members";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error adding member: " + e.getMessage());
            return "redirect:/teams/" + id + "/members";
        }
    }

    /**
     * Remove member from team.
     */
    @PostMapping("/members/{memberId}/remove")
    public String removeMember(@PathVariable Long memberId, 
                              @RequestParam Long teamId,
                              RedirectAttributes redirectAttributes) {
        try {
            Team team = teamService.getTeamById(teamId);
            User member = userService.getUserById(memberId);
            teamService.removeMemberFromTeam(team, member);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Member removed successfully!");
            return "redirect:/teams/" + teamId + "/members";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error removing member: " + e.getMessage());
            return "redirect:/teams/" + teamId + "/members";
        }
    }

    /**
     * Update member's commit tracking username.
     */
    @PostMapping("/members/{memberId}/username")
    @ResponseBody
    public String updateMemberUsername(@PathVariable Long memberId, 
                                     @RequestBody java.util.Map<String, String> request) {
        try {
            String newUsername = request.get("username");
            userService.updateUserNickname(memberId, newUsername);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}
