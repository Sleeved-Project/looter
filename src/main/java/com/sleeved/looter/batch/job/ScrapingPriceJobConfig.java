package com.sleeved.looter.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.sleeved.looter.batch.listener.ScrapingPriceListener;
import com.sleeved.looter.batch.tasklet.FetchAndStagePricesTasklet;

@Configuration
public class ScrapingPriceJobConfig {
  @Value("${looter.batch.chunksize:1}")
  private Integer chunkSize;

  @Autowired
  private ScrapingPriceListener scrapingPriceListener;

  @Bean
  public Job scrapingPriceJob(JobRepository jobRepository, Step fetchPricesStageStep) {
    return new JobBuilder("scrapingPriceJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(scrapingPriceListener)
        .start(fetchPricesStageStep)
        .build();
  }

  @Bean
  public Step fetchPricesStageStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FetchAndStagePricesTasklet fetchAndStagePricesTasklet) {
    return new StepBuilder("fetchPricesStageStep", jobRepository)
        .listener(scrapingPriceListener)
        .tasklet(fetchAndStagePricesTasklet, transactionManager)
        .build();
  }
}
