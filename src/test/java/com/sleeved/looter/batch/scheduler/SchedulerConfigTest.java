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

import com.sleeved.looter.infra.service.DockerJobLauncherService;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigTest {

    @Mock
    private JobExecutionCheckerService jobExecutionCheckerService;

    @Mock
    private DockerJobLauncherService dockerJobLauncherService;

    @InjectMocks
    private SchedulerConfig schedulerConfig;

    @Test
    void triggerScrapingPriceJob_shouldSkipExecution_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingPriceJob"), any(LocalDate.class)))
            .thenReturn(true);

        // When
        schedulerConfig.triggerScrapingPriceJob();

        // Then
        verify(dockerJobLauncherService, never()).runJobInEphemeralContainer(any(), any());
    }

    @Test
    void triggerScrapingPriceJob_shouldExecuteJob_whenJobNotCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingPriceJob"), any(LocalDate.class)))
            .thenReturn(false);
        doNothing().when(dockerJobLauncherService).runJobInEphemeralContainer("scrapingPriceJob", "local");

        // When
        schedulerConfig.triggerScrapingPriceJob();

        // Then
        verify(dockerJobLauncherService).runJobInEphemeralContainer("scrapingPriceJob", "local");
    }

    @Test
    void triggerScrapingPriceJob_shouldHandleException_whenDockerServiceFails() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingPriceJob"), any(LocalDate.class)))
            .thenReturn(false);
        doThrow(new RuntimeException("Docker container failed"))
            .when(dockerJobLauncherService).runJobInEphemeralContainer("scrapingPriceJob", "local");

        // When & Then - Should not throw exception, but log it
        schedulerConfig.triggerScrapingPriceJob();

        verify(dockerJobLauncherService).runJobInEphemeralContainer("scrapingPriceJob", "local");
    }

    @Test
    void triggerScrapingCardJob_shouldSkipExecution_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingCardJob"), any(LocalDate.class)))
            .thenReturn(true);

        // When
        schedulerConfig.triggerScrapingCardJob();

        // Then
        verify(dockerJobLauncherService, never()).runJobInEphemeralContainer(any(), any());
    }

    @Test
    void triggerScrapingCardJob_shouldExecuteJob_whenJobNotCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingCardJob"), any(LocalDate.class)))
            .thenReturn(false);
        doNothing().when(dockerJobLauncherService).runJobInEphemeralContainer("scrapingCardJob", "local");

        // When
        schedulerConfig.triggerScrapingCardJob();

        // Then
        verify(dockerJobLauncherService).runJobInEphemeralContainer("scrapingCardJob", "local");
    }

    @Test
    void triggerScrapingCardJob_shouldHandleException_whenDockerServiceFails() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("scrapingCardJob"), any(LocalDate.class)))
            .thenReturn(false);
        doThrow(new RuntimeException("Docker container failed"))
            .when(dockerJobLauncherService).runJobInEphemeralContainer("scrapingCardJob", "local");

        // When & Then - Should not throw exception, but log it
        schedulerConfig.triggerScrapingCardJob();

        verify(dockerJobLauncherService).runJobInEphemeralContainer("scrapingCardJob", "local");
    }

    @Test
    void triggerHashingCardImageJob_shouldSkipExecution_whenJobAlreadyCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("hashingCardImageJob"), any(LocalDate.class)))
            .thenReturn(true);

        // When
        schedulerConfig.triggerHashingCardImageJob();

        // Then
        verify(dockerJobLauncherService, never()).runJobInEphemeralContainer(any(), any());
    }

    @Test
    void triggerHashingCardImageJob_shouldExecuteJob_whenJobNotCompletedToday() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("hashingCardImageJob"), any(LocalDate.class)))
            .thenReturn(false);
        doNothing().when(dockerJobLauncherService).runJobInEphemeralContainer("hashingCardImageJob", "local");

        // When
        schedulerConfig.triggerHashingCardImageJob();

        // Then
        verify(dockerJobLauncherService).runJobInEphemeralContainer("hashingCardImageJob", "local");
    }

    @Test
    void triggerHashingCardImageJob_shouldHandleException_whenDockerServiceFails() throws Exception {
        // Given
        when(jobExecutionCheckerService.isJobCompletedOnDate(eq("hashingCardImageJob"), any(LocalDate.class)))
            .thenReturn(false);
        doThrow(new RuntimeException("Docker container failed"))
            .when(dockerJobLauncherService).runJobInEphemeralContainer("hashingCardImageJob", "local");

        // When & Then - Should not throw exception, but log it
        schedulerConfig.triggerHashingCardImageJob();

        verify(dockerJobLauncherService).runJobInEphemeralContainer("hashingCardImageJob", "local");
    }
}
