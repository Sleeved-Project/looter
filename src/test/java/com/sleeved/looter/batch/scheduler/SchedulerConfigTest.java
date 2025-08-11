package com.sleeved.looter.batch.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
class SchedulerConfigTest {

    @Mock
    private JobExecutionCheckerService jobExecutionCheckerService;

    @InjectMocks
    private SchedulerConfig schedulerConfig;

    @Mock
    private JobRunnerService jobRunnerService;

    @Test
    void triggerScrapingPriceJob_shouldSkipExecution_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingPriceJob"), any(LocalDate.class)))
            .thenReturn(true);

        // When
        schedulerConfig.triggerScrapingPriceJob();

        // Then
        verify(jobRunnerService, never()).runJob(any(), any());
    }

    @Test
    void triggerScrapingPriceJob_shouldExecuteJob_whenJobNotCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingPriceJob"), any(LocalDate.class)))
            .thenReturn(false);
        doNothing().when(jobRunnerService).runJob("scrapingPriceJob", "local");

        // When
        schedulerConfig.triggerScrapingPriceJob();

        // Then
        verify(jobRunnerService).runJob("scrapingPriceJob", "local");
    }

    @Test
    void triggerScrapingPriceJob_shouldHandleException_whenDockerServiceFails() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingPriceJob"), any(LocalDate.class)))
            .thenReturn(false);
        doThrow(new RuntimeException("Docker container failed"))
            .when(jobRunnerService).runJob("scrapingPriceJob", "local");

        // When & Then - Should not throw exception, but log it
        schedulerConfig.triggerScrapingPriceJob();

        verify(jobRunnerService).runJob("scrapingPriceJob", "local");
    }

    @Test
    void triggerScrapingCardJob_shouldSkipExecution_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingCardJob"), any(LocalDate.class)))
            .thenReturn(true);

        // When
        schedulerConfig.triggerScrapingCardJob();

        // Then
        verify(jobRunnerService, never()).runJob(any(), any());
    }

    @Test
    void triggerScrapingCardJob_shouldExecuteJob_whenJobNotCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingCardJob"), any(LocalDate.class)))
            .thenReturn(false);
        doNothing().when(jobRunnerService).runJob("scrapingCardJob", "local");

        // When
        schedulerConfig.triggerScrapingCardJob();

        // Then
        verify(jobRunnerService).runJob("scrapingCardJob", "local");
    }

    @Test
    void triggerScrapingCardJob_shouldHandleException_whenDockerServiceFails() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingCardJob"), any(LocalDate.class)))
            .thenReturn(false);
        doThrow(new RuntimeException("Docker container failed"))
            .when(jobRunnerService).runJob("scrapingCardJob", "local");

        // When & Then - Should not throw exception, but log it
        schedulerConfig.triggerScrapingCardJob();

        verify(jobRunnerService).runJob("scrapingCardJob", "local");
    }

    @Test
    void triggerHashingCardImageJob_shouldSkipExecution_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("hashingCardImageJob"), any(LocalDate.class)))
            .thenReturn(true);

        // When
        schedulerConfig.triggerHashingCardImageJob();

        // Then
        verify(jobRunnerService, never()).runJob(any(), any());
    }

    @Test
    void triggerHashingCardImageJob_shouldExecuteJob_whenJobNotCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("hashingCardImageJob"), any(LocalDate.class)))
            .thenReturn(false);
        doNothing().when(jobRunnerService).runJob("hashingCardImageJob", "local");

        // When
        schedulerConfig.triggerHashingCardImageJob();

        // Then
        verify(jobRunnerService).runJob("hashingCardImageJob", "local");
    }

    @Test
    void triggerHashingCardImageJob_shouldHandleException_whenDockerServiceFails() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("hashingCardImageJob"), any(LocalDate.class)))
            .thenReturn(false);
        doThrow(new RuntimeException("Docker container failed"))
            .when(jobRunnerService).runJob("hashingCardImageJob", "local");

        // When & Then - Should not throw exception, but log it
        schedulerConfig.triggerHashingCardImageJob();

        verify(jobRunnerService).runJob("hashingCardImageJob", "local");
    }
}
