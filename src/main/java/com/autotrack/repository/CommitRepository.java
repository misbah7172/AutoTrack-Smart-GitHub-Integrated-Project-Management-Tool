package com.autotrack.repository;

import com.autotrack.model.Commit;
import com.autotrack.model.Project;
import com.autotrack.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Commit entity.
 */
@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    
    List<Commit> findByTaskOrderByCommittedAtDesc(Task task);
    
    List<Commit> findByProjectOrderByCommittedAtDesc(Project project);
    
    Optional<Commit> findByShaAndProject(String sha, Project project);
}
