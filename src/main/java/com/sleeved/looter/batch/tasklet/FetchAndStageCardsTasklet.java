package com.sleeved.looter.batch.tasklet;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.staging.StagingCard;
import com.sleeved.looter.domain.repository.staging.StagingCardRepository;
import com.sleeved.looter.infra.mapper.StagingCardMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.infra.service.TcgApiService;

@Component
public class FetchAndStageCardsTasklet implements Tasklet {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_DELAY_MS = 2000; // 2 secondes
    private static final double BACKOFF_MULTIPLIER = 2.0; // Délai exponentiel

    private final TcgApiService tcgApiService;
    private final StagingCardRepository stagingCardRepo;
    private final StagingCardMapper stagingCardMapper;
    private final LooterScrapingErrorHandler looterScrapingErrorHandler;

    public FetchAndStageCardsTasklet(
            TcgApiService tcgApiService,
            StagingCardRepository stagingCardRepo,
            StagingCardMapper stagingCardMapper,
            LooterScrapingErrorHandler looterScrapingErrorHandler) {
        this.tcgApiService = tcgApiService;
        this.stagingCardRepo = stagingCardRepo;
        this.stagingCardMapper = stagingCardMapper;
        this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Long jobId = chunkContext.getStepContext().getStepExecution().getJobExecution().getId();
        LocalDateTime now = LocalDateTime.now();

        int attempt = 0;
        Exception lastException = null;
        long currentDelay = INITIAL_DELAY_MS;
        
        while (attempt < MAX_RETRY_ATTEMPTS) {
            try {
                attempt++;
                List<JsonNode> cards = tcgApiService.fetchAllCards();
                
                List<StagingCard> entities = stagingCardMapper.toEntities(cards, jobId, now);

                stagingCardRepo.saveAll(entities);

                return RepeatStatus.FINISHED;
            } catch (Exception e) {
                lastException = e;
                if (isRetryableError(e)) {
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        Thread.sleep(currentDelay);
                        currentDelay = (long) (currentDelay * BACKOFF_MULTIPLIER);
                    } else {
                        looterScrapingErrorHandler.handle(lastException, 
                          Constantes.STAGE_CARD_TASKLET_CONTEXT, 
                          Constantes.EXECUTE_ACTION, 
                          Constantes.STAGING_CARD_ITEM);
                        throw lastException;
                    }
                } else {
                    looterScrapingErrorHandler.handle(e, 
                        Constantes.STAGE_CARD_TASKLET_CONTEXT, 
                        Constantes.EXECUTE_ACTION, 
                        Constantes.STAGING_CARD_ITEM);
                    throw e;
                }
            }
        }
        
        throw lastException != null ? lastException : 
            new RuntimeException("Failed after " + MAX_RETRY_ATTEMPTS + " attempts");
    }
    
    private boolean isRetryableError(Exception exception) {
        if (exception instanceof HttpClientErrorException) {
            HttpClientErrorException httpError = (HttpClientErrorException) exception;
            int statusCode = httpError.getStatusCode().value();
            
            boolean isRetryable = statusCode == 404 || statusCode == 429 || statusCode >= 500;
            return isRetryable;
        }
        
        boolean isNetworkError = exception instanceof ConnectException 
            || exception instanceof SocketTimeoutException
            || exception instanceof UnknownHostException
            || (exception.getCause() instanceof ConnectException);
        
        return isNetworkError;
    }
}