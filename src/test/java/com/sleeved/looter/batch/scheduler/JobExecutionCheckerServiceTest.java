package com.sleeved.looter.batch.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;

@ExtendWith(MockitoExtension.class)
class JobExecutionCheckerServiceTest {

    @Mock
    private JobExplorer jobExplorer;

    @InjectMocks
    private JobExecutionCheckerService jobExecutionCheckerService;

    @Test
    void isJobCompletedOnDate_shouldReturnTrue_whenJobCompletedOnTargetDate() {
        // Given
        String jobName = "testJob";
        LocalDate targetDate = LocalDate.of(2025, 7, 30);
        
        JobInstance jobInstance = createJobInstance(1L, jobName);
        JobExecution completedExecution = createJobExecution(1L, 
            BatchStatus.COMPLETED, 
            LocalDateTime.of(2025, 7, 30, 10, 0));

        when(jobExplorer.findJobInstancesByJobName(eq(jobName), eq(0), eq(10)))
            .thenReturn(Arrays.asList(jobInstance));
        when(jobExplorer.getJobExecutions(jobInstance))
            .thenReturn(Arrays.asList(completedExecution));

        // When
        boolean result = jobExecutionCheckerService.isJobCompletedOnDate(jobName, targetDate);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isJobCompletedOnDate_shouldReturnFalse_whenJobCompletedOnDifferentDate() {
        // Given
        String jobName = "testJob";
        LocalDate targetDate = LocalDate.of(2025, 7, 30);
        
        JobInstance jobInstance = createJobInstance(1L, jobName);
        JobExecution completedExecution = createJobExecution(1L, 
            BatchStatus.COMPLETED, 
            LocalDateTime.of(2025, 7, 29, 10, 0)); // Different date

        when(jobExplorer.findJobInstancesByJobName(eq(jobName), eq(0), eq(10)))
            .thenReturn(Arrays.asList(jobInstance));
        when(jobExplorer.getJobExecutions(jobInstance))
            .thenReturn(Arrays.asList(completedExecution));

        // When
        boolean result = jobExecutionCheckerService.isJobCompletedOnDate(jobName, targetDate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isJobCompletedOnDate_shouldReturnFalse_whenJobFailedOnTargetDate() {
        // Given
        String jobName = "testJob";
        LocalDate targetDate = LocalDate.of(2025, 7, 30);
        
        JobInstance jobInstance = createJobInstance(1L, jobName);
        JobExecution failedExecution = createJobExecution(1L, 
            BatchStatus.FAILED, 
            LocalDateTime.of(2025, 7, 30, 10, 0));

        when(jobExplorer.findJobInstancesByJobName(eq(jobName), eq(0), eq(10)))
            .thenReturn(Arrays.asList(jobInstance));
        when(jobExplorer.getJobExecutions(jobInstance))
            .thenReturn(Arrays.asList(failedExecution));

        // When
        boolean result = jobExecutionCheckerService.isJobCompletedOnDate(jobName, targetDate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isJobCompletedOnDate_shouldReturnFalse_whenNoJobInstancesFound() {
        // Given
        String jobName = "nonExistentJob";
        LocalDate targetDate = LocalDate.of(2025, 7, 30);

        when(jobExplorer.findJobInstancesByJobName(eq(jobName), eq(0), eq(10)))
            .thenReturn(Collections.emptyList());

        // When
        boolean result = jobExecutionCheckerService.isJobCompletedOnDate(jobName, targetDate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isJobCompletedOnDate_shouldReturnFalse_whenExceptionOccurs() {
        // Given
        String jobName = "testJob";
        LocalDate targetDate = LocalDate.of(2025, 7, 30);

        when(jobExplorer.findJobInstancesByJobName(anyString(), anyInt(), anyInt()))
            .thenThrow(new RuntimeException("Database connection error"));

        // When
        boolean result = jobExecutionCheckerService.isJobCompletedOnDate(jobName, targetDate);

        // Then
        assertThat(result).isFalse();
    }

    private JobInstance createJobInstance(Long id, String jobName) {
        JobInstance jobInstance = new JobInstance(id, jobName);
        return jobInstance;
    }

    private JobExecution createJobExecution(Long id, BatchStatus status, LocalDateTime startTime) {
        JobExecution execution = new JobExecution(id);
        execution.setStatus(status);
        execution.setStartTime(startTime);
        return execution;
    }
}
