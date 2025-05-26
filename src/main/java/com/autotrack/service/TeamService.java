package com.autotrack.service;

import com.autotrack.dto.TeamDTO;
import com.autotrack.model.Role;
import com.autotrack.model.Team;
import com.autotrack.model.User;
import com.autotrack.repository.TeamRepository;
import com.autotrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing teams.
 */
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all teams.
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Get teams for a user.
     */
    public List<Team> getTeamsByUser(User user) {
        return teamRepository.findTeamsByUser(user);
    }

    /**
     * Get a team by ID.
     */
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + id));
    }

    /**
     * Create a new team.
     */
    @Transactional
    public Team createTeam(TeamDTO teamDTO, User creator) {
        // Get members
        List<User> members = teamDTO.getMemberIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + id)))
                .collect(Collectors.toList());
        
        // Make sure creator is in the team
        if (!members.contains(creator)) {
            members.add(creator);
        }
        
        // Make creator a team lead if not already
        if (!creator.hasRole("TEAM_LEAD")) {
            List<Role> roles = creator.getRoles();
            roles.add(Role.TEAM_LEAD);
            creator.setRoles(roles);
            userRepository.save(creator);
        }
        
        // Create the team
        Team team = Team.builder()
                .name(teamDTO.getName())
                .members(members)
                .build();
        
        return teamRepository.save(team);
    }

    /**
     * Update an existing team.
     */
    @Transactional
    public Team updateTeam(Long id, TeamDTO teamDTO) {
        Team existingTeam = getTeamById(id);
        
        // Get members
        List<User> members = teamDTO.getMemberIds().stream()
                .map(memberId -> userRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + memberId)))
                .collect(Collectors.toList());
        
        // Update team
        existingTeam.setName(teamDTO.getName());
        existingTeam.setMembers(members);
        
        return teamRepository.save(existingTeam);
    }

    /**
     * Delete a team.
     */
    @Transactional
    public void deleteTeam(Long id) {
        Team team = getTeamById(id);
        
        // Cannot delete a team with projects
        if (!team.getProjects().isEmpty()) {
            throw new RuntimeException("Cannot delete a team with projects. Delete the projects first.");
        }
        
        teamRepository.delete(team);
    }

    /**
     * Add member to team.
     */
    @Transactional
    public void addMemberToTeam(Team team, User member) {
        if (!team.getMembers().contains(member)) {
            team.getMembers().add(member);
            teamRepository.save(team);
        }
    }

    /**
     * Remove member from team.
     */
    @Transactional
    public void removeMemberFromTeam(Team team, User member) {
        team.getMembers().remove(member);
        teamRepository.save(team);
    }
}
