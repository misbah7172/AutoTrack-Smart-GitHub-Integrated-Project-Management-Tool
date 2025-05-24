package com.autotrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a GitHub commit.
 */
@Entity
@Table(name = "commits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String sha;
    
    @Column(nullable = false)
    private String message;
    
    @Column(name = "author_name")
    private String authorName;
    
    @Column(name = "author_email")
    private String authorEmail;
    
    @Column(name = "committed_at")
    private LocalDateTime committedAt;
    
    @Column(name = "github_url")
    private String gitHubUrl;
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
