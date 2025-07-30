package com.sleeved.looter.infra.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DockerJobLauncherService {

    private static final int COMMAND_TIMEOUT_MINUTES = 30;

    /**
     * Lance un job dans un container éphémère via Docker Compose
     */
    public void runJobInEphemeralContainer(String jobName, String profile) throws Exception {
        log.info("🐳 Launching ephemeral container for job '{}' with profile '{}'", jobName, profile);
        
        String[] command = {
            "docker", "compose", "run", "--rm", "job-runner",
            "java", "-jar", "app.jar",
            "--job.name=" + jobName,
            "--spring.profiles.active=" + profile,
            "--spring.main.web-application-type=none"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO(); // Pour voir les logs du job dans les logs du scheduler
        
        try {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(COMMAND_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException(String.format(
                    "Job '%s' timed out after %d minutes", jobName, COMMAND_TIMEOUT_MINUTES));
            }
            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException(String.format(
                    "Job '%s' failed with exit code %d", jobName, exitCode));
            }
            
            log.info("✅ Job '{}' completed successfully in ephemeral container", jobName);
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(String.format(
                "Failed to launch ephemeral container for job '%s': %s", jobName, e.getMessage()), e);
        }
    }
}
