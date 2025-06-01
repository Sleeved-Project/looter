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
import com.sleeved.looter.batch.processor.CardPriceDTOToReportingPriceEntityCardProcessor;
import com.sleeved.looter.batch.reader.StagingCardToCardPriceDTOReader;
import com.sleeved.looter.batch.tasklet.FetchAndStagePricesTasklet;
import com.sleeved.looter.batch.writer.ReportingPriceEntityWriter;
import com.sleeved.looter.infra.dto.CardPriceDTO;
import com.sleeved.looter.infra.dto.ReportingPriceEntitiesProcessedDTO;

@Configuration
public class ScrapingPriceJobConfig {
  @Value("${looter.batch.chunksize:1}")
  private Integer chunkSize;

  @Autowired
  private ScrapingPriceListener scrapingPriceListener;

  @Bean
  public Job scrapingPriceJob(JobRepository jobRepository, Step fetchPricesStageStep,
      Step importReportingPricesStep) {
    return new JobBuilder("scrapingPriceJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(scrapingPriceListener)
        .start(fetchPricesStageStep)
        .next(importReportingPricesStep)
        .build();
  }

  @Bean
  public Job scrapingPriceJobWithoutApi(JobRepository jobRepository, Step importReportingPricesStep) {
    return new JobBuilder("scrapingPriceJobWithoutApi", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(scrapingPriceListener)
        .start(importReportingPricesStep)
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

  @Bean
  public Step importReportingPricesStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      StagingCardToCardPriceDTOReader reader,
      CardPriceDTOToReportingPriceEntityCardProcessor processor,
      ReportingPriceEntityWriter writer) {
    return new StepBuilder("importReportingPricesStep", jobRepository)
        .<CardPriceDTO, ReportingPriceEntitiesProcessedDTO>chunk(chunkSize, transactionManager)
        .listener(scrapingPriceListener)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }
}
