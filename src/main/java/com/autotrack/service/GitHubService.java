package com.autotrack.service;

import com.autotrack.model.Project;
import com.autotrack.model.Task;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service for GitHub API interactions.
 */
@Service
public class GitHubService {
    
    private static final String GITHUB_API_URL = "https://api.github.com";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Create a GitHub issue for a task.
     * 
     * @param task The task to create an issue for
     * @param project The project containing the task
     * @return The URL of the created issue, or null if creation failed
     */
    public String createGitHubIssue(Task task, Project project) {
        if (project.getGitHubAccessToken() == null || project.getGitHubRepoUrl() == null) {
            return null;
        }
        
        // Extract owner and repo from GitHub URL
        String repoUrl = project.getGitHubRepoUrl();
        String[] parts = repoUrl.replace("https://github.com/", "").split("/");
        if (parts.length < 2) {
            return null;
        }
        
        String owner = parts[0];
        String repo = parts[1];
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create issue object
            ObjectNode issueJson = objectMapper.createObjectNode();
            issueJson.put("title", task.getFeatureCode() + ": " + task.getTitle());
            
            // Create description with task details
            StringBuilder description = new StringBuilder();
            description.append("## Task Details\n\n");
            description.append("- **Feature Code:** ").append(task.getFeatureCode()).append("\n");
            description.append("- **Status:** ").append(task.getStatus()).append("\n");
            
            if (task.getAssignee() != null) {
                description.append("- **Assignee:** ").append(task.getAssignee().getNickname()).append("\n");
            }
            
            if (task.getMilestone() != null && !task.getMilestone().isEmpty()) {
                description.append("- **Milestone:** ").append(task.getMilestone()).append("\n");
            }
            
            if (!task.getTags().isEmpty()) {
                description.append("- **Tags:** ").append(String.join(", ", task.getTags())).append("\n");
            }
            
            description.append("\n*This issue was automatically created by AutoTrack.*");
            issueJson.put("body", description.toString());
            
            // Create labels based on tags
            if (!task.getTags().isEmpty()) {
                JsonNode labelsNode = objectMapper.createArrayNode();
                for (String tag : task.getTags()) {
                    ((com.fasterxml.jackson.databind.node.ArrayNode) labelsNode).add(tag);
                }
                issueJson.set("labels", labelsNode);
            }
            
            // Create HTTP request
            HttpPost httpPost = new HttpPost(GITHUB_API_URL + "/repos/" + owner + "/" + repo + "/issues");
            httpPost.setHeader("Authorization", "token " + project.getGitHubAccessToken());
            httpPost.setHeader("Accept", "application/vnd.github.v3+json");
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(issueJson), ContentType.APPLICATION_JSON));
            
            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getCode() == 201) {
                    JsonNode responseJson = objectMapper.readTree(response.getEntity().getContent());
                    return responseJson.get("html_url").asText();
                }
            }
        } catch (IOException e) {
            // Log error
            System.err.println("Error creating GitHub issue: " + e.getMessage());
        }
        
        return null;
    }
}
