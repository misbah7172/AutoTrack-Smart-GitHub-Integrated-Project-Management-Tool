package com.autotrack.controller;

import com.autotrack.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for GitHub webhook integration.
 */
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Endpoint to receive GitHub webhook events.
     * 
     * @param payload The GitHub webhook payload
     * @param signature The GitHub webhook signature header (X-Hub-Signature-256)
     * @param event The GitHub event type header (X-GitHub-Event)
     * @return Response with appropriate status
     */
    @PostMapping("/github")
    public ResponseEntity<String> handleGitHubWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader(value = "X-GitHub-Event", required = false) String event) {
        
        // Only process push events
        if (event == null || !event.equals("push")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Event ignored: " + (event != null ? event : "unknown"));
        }
        
        try {
            webhookService.processWebhook(payload, signature);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Webhook processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }
}
