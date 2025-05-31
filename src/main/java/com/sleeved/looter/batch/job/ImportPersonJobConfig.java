package com.sleeved.looter.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.sleeved.looter.batch.processor.PersonItemProcessor;
import com.sleeved.looter.batch.reader.PersonItemReader;
import com.sleeved.looter.batch.writer.PersonItemWriter;
import com.sleeved.looter.domain.entity.Person;

@Configuration
public class ImportPersonJobConfig {
  @Value("${looter.batch.chunksize:1}")
  private Integer chunkSize;

  @Bean
  public Job importPersonJob(JobRepository jobRepository, Step stepExemple) {
    return new JobBuilder("importPersonJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(stepExemple)
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
