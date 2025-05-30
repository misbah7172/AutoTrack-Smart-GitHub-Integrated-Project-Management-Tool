package com.autotrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for AutoTrack.
 * Automated task tracking through GitHub commit messages with Kanban-style project management.
 */
@SpringBootApplication
@EnableAsync
public class AutoTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoTrackApplication.class, args);
    }
}
