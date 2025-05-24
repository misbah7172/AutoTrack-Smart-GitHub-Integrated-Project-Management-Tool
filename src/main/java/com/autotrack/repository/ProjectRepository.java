package com.autotrack.repository;

import com.autotrack.model.Project;
import com.autotrack.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Project entity.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByTeam(Team team);
    
    Optional<Project> findByGitHubRepoId(String gitHubRepoId);
    
    @Query("SELECT p FROM Project p JOIN p.team t JOIN t.members m WHERE m.id = :userId")
    List<Project> findProjectsByUserId(Long userId);
}
