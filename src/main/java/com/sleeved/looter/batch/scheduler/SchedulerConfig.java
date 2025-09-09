package com.sleeved.looter.batch.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.exception.LooterSchedulerException;
import com.sleeved.looter.infra.config.JobRunnerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulerConfig {

    private final JobExecutionCheckerService jobExecutionCheckerService;
    private final JobRunnerService jobRunnerService;

    @Scheduled(cron = "0 0 9 * * *") // every day at 9am
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
            LooterSchedulerException schedulerException = new LooterSchedulerException(
                    String.format("Scheduler job '%s' (daily) failed: %s", jobName, e.getMessage()), e);
            log.error("Error while running the job '{}': {}", jobName, schedulerException, e);
        }
    }

    @Scheduled(cron = "0 30 9 1 1,4,7,10 *") // quarterly (Jan/Apr/Jul/Oct) on 1st at 9:30am
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
            LooterSchedulerException schedulerException = new LooterSchedulerException(
                    String.format("Scheduler job '%s' (quarterly) failed: %s", jobName, e.getMessage()), e);
            log.error("Error while running the job '{}': {}", jobName, schedulerException.getMessage(),
                    schedulerException);
        }
    }

    @Scheduled(cron = "0 0 10 1 1,4,7,10 *") // quarterly (Jan/Apr/Jul/Oct) on 1st at 10am
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
            LooterSchedulerException schedulerException = new LooterSchedulerException(
                    String.format("Scheduler job '%s' (quarterly, after scrapingCardJob) failed: %s", jobName,
                            e.getMessage()),
                    e);
            log.error("Error while running the job '{}': {}", jobName, schedulerException.getMessage(),
                    schedulerException);
        }
    }
}
