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

import com.sleeved.looter.batch.listener.HashingImageListener;
import com.sleeved.looter.batch.processor.CardImageDTOToHashCardProcessor;
import com.sleeved.looter.batch.reader.CardToCardImageDTOReader;
import com.sleeved.looter.batch.writer.HashImageWriter;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.infra.dto.CardImageDTO;

@Configuration
public class HashingCardImageJobConfig {
    @Value("${looter.batch.chunksize:1}")
    private Integer chunkSize;

    @Autowired
    private HashingImageListener hashingImageListener;

    @Bean
    public Job hashingCardImageJob(JobRepository jobRepository, Step hashImagesStep) {
        return new JobBuilder("hashingCardImageJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(hashingImageListener)
            .start(hashImagesStep)
            .build();
    } 

    @Bean
    public Step hashImagesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            CardToCardImageDTOReader cardToCardImageDTOReader,
            CardImageDTOToHashCardProcessor cardImageDTOToHashCardProcessor,
            HashImageWriter hashImageWriter
            ) {
        return new StepBuilder("hashImagesStep", jobRepository)
            .<CardImageDTO, HashCard>chunk(chunkSize, transactionManager)
            .listener(hashingImageListener)
            .reader(cardToCardImageDTOReader)
            .processor(cardImageDTOToHashCardProcessor)
            .writer(hashImageWriter)
            .build();
    }
}