package com.autotrack.repository;

import com.autotrack.model.Project;
import com.autotrack.model.Task;
import com.autotrack.model.TaskStatus;
import com.autotrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Task entity.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByProjectAndStatus(Project project, TaskStatus status);
    
    List<Task> findByProjectOrderByUpdatedAtDesc(Project project);
    
    List<Task> findByAssigneeOrderByUpdatedAtDesc(User assignee);
    
    Optional<Task> findByFeatureCodeAndProject(String featureCode, Project project);
}
