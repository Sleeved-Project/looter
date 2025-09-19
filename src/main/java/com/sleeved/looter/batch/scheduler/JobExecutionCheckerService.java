package com.sleeved.looter.batch.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobExecutionCheckerService {

    private final JobExplorer jobExplorer;

    /**
     * Verify if a job has been completed on a specific date
     */
    public boolean isJobCompletedOnDate(String jobName, LocalDate date) {
        try {
            List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName(jobName, 0, 10);

            boolean found = jobInstances.stream()
                    .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream())
                    .anyMatch(execution -> isExecutionCompletedOnDate(execution, date));

            if (found)
                log.info("Job '{}' found completed on {}", jobName, date);
            else
                log.warn("No completed execution found for job '{}' on {}", jobName, date);

            return found;

        } catch (Exception e) {
            log.error("Error while checking job '{}' : {}", jobName, e.getMessage());
            return false;
        }
    }

    /**
     * Verify if a job execution is completed on a specific date
     */
    private boolean isExecutionCompletedOnDate(JobExecution execution, LocalDate targetDate) {
        LocalDateTime startTime = execution.getStartTime();
        return execution.getStatus() == BatchStatus.COMPLETED
                && startTime != null
                && startTime.toLocalDate().equals(targetDate);
    }

}
