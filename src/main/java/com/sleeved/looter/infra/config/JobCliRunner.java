package com.sleeved.looter.infra.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JobCliRunner implements ApplicationRunner {

    private final JobRunnerService jobRunnerService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("job.name")) {
            String jobName = args.getOptionValues("job.name").get(0);
            String profile = args.getOptionValues("spring.profiles.active") != null
                ? args.getOptionValues("spring.profiles.active").get(0)
                : "local";

            jobRunnerService.runJob(jobName, profile);
            System.exit(0);
        }
    }
}

