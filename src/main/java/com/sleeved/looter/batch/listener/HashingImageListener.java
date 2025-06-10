package com.sleeved.looter.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HashingImageListener implements JobExecutionListener, StepExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("---------------------- HASHING IMAGES START ----------------------");
    log.info("Job: Starting {}...\n", jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("--------------------- HASHING IMAGES REPORT ---------------------");
    log.info("Job status: {}\n", jobExecution.getStatus());

    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
      log.info("Step '{}' statistics:", stepExecution.getStepName());
      log.info("- Read count: {}", stepExecution.getReadCount());
      log.info("- Process count: {}", stepExecution.getReadCount() - stepExecution.getProcessSkipCount());
      log.info("- Write count: {}", stepExecution.getWriteCount());
      log.info("- Skip count: {}", stepExecution.getSkipCount());

      if (stepExecution.getFailureExceptions().size() > 0) {
        log.error("- Failures: {}", stepExecution.getFailureExceptions().size());
        stepExecution.getFailureExceptions().forEach(ex -> log.error("- Failure details: {}", ex.getMessage()));
      }
      log.info(Constantes.LINE_BREAK);
    }
  }
}