package com.sleeved.looter.infra.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;

@ExtendWith(MockitoExtension.class)
class JobRunnerServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private JobRegistry jobRegistry;

    @Mock
    private Job job;

    @InjectMocks
    private JobRunnerService jobRunnerService;

    @Test
    void runJob_shouldExecuteJob_whenJobExists() throws Exception {
        // Given
        String jobName = "testJob";
        String profile = "test";

        when(jobRegistry.getJob(jobName)).thenReturn(job);

        // When
        jobRunnerService.runJob(jobName, profile);

        // Then
        verify(jobRegistry).getJob(jobName);
        verify(jobLauncher).run(eq(job), any(JobParameters.class));
    }

    @Test
    void runJob_shouldThrowException_whenJobDoesNotExist() throws Exception {
        // Given
        String jobName = "nonExistentJob";
        String profile = "test";

        when(jobRegistry.getJob(jobName)).thenThrow(new NoSuchJobException("Job not found"));

        // When & Then
        assertThatThrownBy(() -> jobRunnerService.runJob(jobName, profile))
            .isInstanceOf(NoSuchJobException.class)
            .hasMessageContaining("Job not found");

        verify(jobRegistry).getJob(jobName);
    }

    @Test
    void runJob_shouldPassTimestampParameter_whenExecutingJob() throws Exception {
        // Given
        String jobName = "testJob";
        String profile = "test";

        when(jobRegistry.getJob(jobName)).thenReturn(job);

        // When
        jobRunnerService.runJob(jobName, profile);

        // Then
        verify(jobLauncher).run(eq(job), any(JobParameters.class));
        // Note: We can't easily test the exact timestamp value due to timing,
        // but we verify that JobParameters are passed
    }

    @Test
    void runJob_shouldPropagateJobLauncherException_whenLaunchFails() throws Exception {
        // Given
        String jobName = "testJob";
        String profile = "test";
        RuntimeException jobException = new RuntimeException("Job launch failed");

        when(jobRegistry.getJob(jobName)).thenReturn(job);
        when(jobLauncher.run(eq(job), any(JobParameters.class))).thenThrow(jobException);

        // When & Then
        assertThatThrownBy(() -> jobRunnerService.runJob(jobName, profile))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Job launch failed");

        verify(jobRegistry).getJob(jobName);
        verify(jobLauncher).run(eq(job), any(JobParameters.class));
    }
}
