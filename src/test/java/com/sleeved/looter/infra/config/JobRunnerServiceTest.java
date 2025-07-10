package com.sleeved.looter.infra.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;

@ExtendWith(MockitoExtension.class)
public class JobRunnerServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private JobRegistry jobRegistry;

    @InjectMocks
    private JobRunnerService jobRunnerService;

    @Test
    void runJob_shouldLaunchJobWithCorrectParameters() throws Exception {
        Job mockJob = mock(Job.class);
        JobExecution mockExecution = mock(JobExecution.class);
        
        when(jobRegistry.getJob("testJob")).thenReturn(mockJob);
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(mockExecution);

        jobRunnerService.runJob("testJob", "local");

        verify(jobRegistry).getJob("testJob");
        
        ArgumentCaptor<JobParameters> parametersCaptor = ArgumentCaptor.forClass(JobParameters.class);
        verify(jobLauncher).run(eq(mockJob), parametersCaptor.capture());
        
        JobParameters capturedParameters = parametersCaptor.getValue();
        assertThat(capturedParameters.getParameters()).containsKey("timestamp");
    }

    @Test
    void runJob_shouldHandleJobNotFound() throws Exception {
        when(jobRegistry.getJob("unknownJob"))
            .thenThrow(new RuntimeException("Job not found"));

        assertThatThrownBy(() -> jobRunnerService.runJob("unknownJob", "local"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Job not found");

        verify(jobRegistry).getJob("unknownJob");
        verifyNoInteractions(jobLauncher);
    }
}