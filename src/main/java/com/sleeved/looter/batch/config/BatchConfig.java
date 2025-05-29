package com.sleeved.looter.batch.config;

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

import com.sleeved.looter.batch.listener.ImportScrappingListener;
import com.sleeved.looter.batch.processor.CardDTOToBaseEntityCardProcessor;
import com.sleeved.looter.batch.processor.CardDTOToSetsWeaknessResistanceCardProcessor;
import com.sleeved.looter.batch.processor.PersonItemProcessor;
import com.sleeved.looter.batch.reader.PersonItemReader;
import com.sleeved.looter.batch.reader.StagingCardToCardDTOReader;
import com.sleeved.looter.batch.tasklet.FetchAndStageCardsTasklet;
import com.sleeved.looter.batch.writer.BaseEntityWriter;
import com.sleeved.looter.batch.writer.PersonItemWriter;
import com.sleeved.looter.batch.writer.SetsWeaknessResistanceWriter;
import com.sleeved.looter.domain.entity.Person;
import com.sleeved.looter.infra.dto.BaseCardEntitiesProcessedDTO;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.SetsWeaknessResistanceCardEntitiesProcessedDTO;

@Configuration
public class BatchConfig {
  @Value("${looter.batch.chunksize:1}")
  private Integer chunkSize;

  @Autowired
  private ImportScrappingListener importScrappingListener;

  @Bean
  public Job importScrapingJob(JobRepository jobRepository, Step fetchCardsStageStep, Step importBaseEntitiesStep,
      Step importSetsWeaknessResitanceStep) {
    return new JobBuilder("importPersonJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(importScrappingListener)
        .start(fetchCardsStageStep)
        .next(importBaseEntitiesStep)
        .next(importSetsWeaknessResitanceStep)
        .build();
  }

  // @Bean
  // public Job importScrapingJob(JobRepository jobRepository, Step
  // fetchCardsStageStep, Step importBaseEntitiesStep,
  // Step importSetsWeaknessResitanceStep) {
  // return new JobBuilder("importScrapingJob", jobRepository)
  // .incrementer(new RunIdIncrementer())
  // .listener(importScrappingListener)
  // .start(importBaseEntitiesStep)
  // .next(importSetsWeaknessResitanceStep)
  // .build();
  // }

  @Bean
  public Step fetchCardsStageStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FetchAndStageCardsTasklet fetchAndStageCardsTasklet) {
    return new StepBuilder("fetchCardsStageStep", jobRepository)
        .listener(importScrappingListener)
        .tasklet(fetchAndStageCardsTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step importBaseEntitiesStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      StagingCardToCardDTOReader reader,
      CardDTOToBaseEntityCardProcessor processor,
      BaseEntityWriter writer) {
    return new StepBuilder("importBaseEntitiesStep", jobRepository)
        .<CardDTO, BaseCardEntitiesProcessedDTO>chunk(chunkSize, transactionManager)
        .listener(importScrappingListener)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  public Step importSetsWeaknessResitanceStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      StagingCardToCardDTOReader reader,
      CardDTOToSetsWeaknessResistanceCardProcessor processor,
      SetsWeaknessResistanceWriter writer) {
    return new StepBuilder("importSetsWeaknessResitanceStep", jobRepository)
        .<CardDTO, SetsWeaknessResistanceCardEntitiesProcessedDTO>chunk(chunkSize, transactionManager)
        .listener(importScrappingListener)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  public Step stepExemple(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      PersonItemReader reader,
      PersonItemProcessor processor,
      PersonItemWriter writer) {
    return new StepBuilder("stepExemple", jobRepository)
        .<Person, Person>chunk(chunkSize, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

}
