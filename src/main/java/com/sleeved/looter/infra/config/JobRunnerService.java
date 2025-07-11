package com.sleeved.looter.infra.config;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JobRunnerService {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    public void runJob(String jobName, String profile) throws Exception {
        Job job = jobRegistry.getJob(jobName);

        JobParameters parameters = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        jobLauncher.run(job, parameters);
    }
}
