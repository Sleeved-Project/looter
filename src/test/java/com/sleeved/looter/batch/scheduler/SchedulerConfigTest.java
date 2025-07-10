package com.sleeved.looter.batch.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.infra.config.JobRunnerService;

@ExtendWith(MockitoExtension.class)
public class SchedulerConfigTest {

    @Mock
    private JobExecutionCheckerService jobExecutionCheckerService;

    @Mock
    private JobRunnerService jobRunnerService;

    @InjectMocks
    private SchedulerConfig schedulerConfig;

    @Test
    void triggerScrapingPriceJob_shouldRunJob_whenJobNotCompletedToday() throws Exception {
        // Given
        String jobName = "scrapingPriceJob";
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq(jobName), any(LocalDate.class))).thenReturn(false);

        // When
        schedulerConfig.triggerScrapingPriceJob();

        // Then
        verify(jobExecutionCheckerService).isJobCompletedOnDate(eq(jobName), any(LocalDate.class));
        verify(jobRunnerService).runJob(jobName, "local");
    }

    @Test
    void triggerScrapingPriceJob_shouldNotRunJob_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        String jobName = "scrapingPriceJob";
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq(jobName), any(LocalDate.class))).thenReturn(true);

        // When
        schedulerConfig.triggerScrapingPriceJob();

        // Then
        verify(jobExecutionCheckerService).isJobCompletedOnDate(eq(jobName), any(LocalDate.class));
        verify(jobRunnerService, never()).runJob(any(), any());
    }

    @Test
    void triggerScrapingCardJob_shouldRunJob_whenJobNotCompletedToday() throws Exception {
        // Given
        String jobName = "scrapingCardJob";
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq(jobName), any(LocalDate.class))).thenReturn(false);

        // When
        schedulerConfig.triggerScrapingCardJob();

        // Then
        verify(jobExecutionCheckerService).isJobCompletedOnDate(eq(jobName), any(LocalDate.class));
        verify(jobRunnerService).runJob(jobName, "local");
    }

    @Test
    void triggerScrapingCardJob_shouldNotRunJob_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        String jobName = "scrapingCardJob";
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq(jobName), any(LocalDate.class))).thenReturn(true);

        // When
        schedulerConfig.triggerScrapingCardJob();

        // Then
        verify(jobExecutionCheckerService).isJobCompletedOnDate(eq(jobName), any(LocalDate.class));
        verify(jobRunnerService, never()).runJob(any(), any());
    }

    @Test
    void triggerHashingCardImageJob_shouldRunJob_whenJobNotCompletedToday() throws Exception {
        // Given
        String jobName = "hashingCardImageJob";
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq(jobName), any(LocalDate.class))).thenReturn(false);

        // When
        schedulerConfig.triggerHashingCardImageJob();

        // Then
        verify(jobExecutionCheckerService).isJobCompletedOnDate(eq(jobName), any(LocalDate.class));
        verify(jobRunnerService).runJob(jobName, "local");
    }

    @Test
    void triggerHashingCardImageJob_shouldNotRunJob_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        String jobName = "hashingCardImageJob";
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq(jobName), any(LocalDate.class))).thenReturn(true);

        // When
        schedulerConfig.triggerHashingCardImageJob();

        // Then
        verify(jobExecutionCheckerService).isJobCompletedOnDate(eq(jobName), any(LocalDate.class));
        verify(jobRunnerService, never()).runJob(any(), any());
    }
}
