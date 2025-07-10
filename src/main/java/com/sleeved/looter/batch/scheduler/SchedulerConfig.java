package com.sleeved.looter.batch.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sleeved.looter.infra.config.JobRunnerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulerConfig {

    private final JobExecutionCheckerService jobExecutionCheckerService;
    private final JobRunnerService jobRunnerService;

    @Scheduled(cron = "0 0 2 * * *") // all days at 2am
    public void triggerScrapingPriceJob() {
        String jobName = "scrapingPriceJob";
        if (jobExecutionCheckerService.isJobCompletedOnDate(jobName, LocalDate.now())) {
            log.info("The '{}' job has already run successfully today.", jobName);
            return;
        }
        try {
            log.info("Triggering the '{}' job (daily)", jobName);
            jobRunnerService.runJob(jobName, "local");
        } catch (Exception e) {
            log.error("Error while running the job '{}': {}", jobName, e.getMessage(), e);        
        }
    }

    @Scheduled(cron = "0 0 3 1 1,4,7,10 *") // all three months at 3am
    public void triggerScrapingCardJob() {
        String jobName = "scrapingCardJob";
        if (jobExecutionCheckerService.isJobCompletedOnDate(jobName, LocalDate.now())) {
            log.info("The '{}' job has already run successfully today.", jobName);
            return;
        }
        try {
            log.info("Triggering the '{}' job (quarterly)", jobName);
            jobRunnerService.runJob(jobName, "local");
        } catch (Exception e) {
            log.error("Error while running the job '{}': {}", jobName, e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 4 1 1,4,7,10 *") // all three months at 4am
    public void triggerHashingCardImageJob() {
        String jobName = "hashingCardImageJob";
        if (jobExecutionCheckerService.isJobCompletedOnDate(jobName, LocalDate.now())) {
            log.info("The '{}' job has already run successfully today.", jobName);
            return;
        }
        try {
            log.info("Triggering the '{}' job (quarterly, after scrapingCardJob)", jobName);
            jobRunnerService.runJob(jobName, "local");
        } catch (Exception e) {
            log.error("Error while running the job '{}': {}", jobName, e.getMessage(), e);
        }
    }
}
