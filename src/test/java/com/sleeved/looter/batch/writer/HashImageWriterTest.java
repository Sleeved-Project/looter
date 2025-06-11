package com.sleeved.looter.batch.writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.service.HashCardService;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.HashCardMock;

@ExtendWith(MockitoExtension.class)
class HashImageWriterTest {

    @Mock
    private HashCardService hashCardService;
    
    @Mock
    private LooterScrapingErrorHandler looterScrapingErrorHandler;
    
    private HashImageWriter writer;
    
    @BeforeEach
    void setUp() {
        writer = new HashImageWriter(hashCardService, looterScrapingErrorHandler);
    }

    @Test
    void write_ShouldCallHashCardServiceForValidCards() throws Exception {
        // Arrange
        HashCard hashCard1 = HashCardMock.createMock("test-card-1", "test-hash-1");
        
        HashCard hashCard2 = HashCardMock.createMock("test-card-2", "test-hash-2");
        
        Chunk<HashCard> chunk = new Chunk<>(Arrays.asList(hashCard1, hashCard2));
        
        // Act
        writer.write(chunk);
        
        // Assert
        verify(hashCardService, times(1)).getOrCreate(hashCard1);
        verify(hashCardService, times(1)).getOrCreate(hashCard2);
        verifyNoInteractions(looterScrapingErrorHandler);
    }
    
    @Test
    void write_ShouldSkipCardsWithNullHash() throws Exception {
        // Arrange
        HashCard validCard = HashCardMock.createMock("test-card-1", "test-hash-1");
        
        HashCard invalidCard = HashCardMock.createMock("test-card-2", null);
        
        Chunk<HashCard> chunk = new Chunk<>(Arrays.asList(validCard, invalidCard));
        
        // Act
        writer.write(chunk);
        
        // Assert
        verify(hashCardService, times(1)).getOrCreate(validCard);
        verify(hashCardService, never()).getOrCreate(invalidCard);
        verifyNoInteractions(looterScrapingErrorHandler);
    }
    
    @Test
    void write_ShouldHandleExceptions() throws Exception {
        // Arrange
        HashCard hashCard = HashCardMock.createMock("test-card-1", "test-hash-1");
        
        Chunk<HashCard> chunk = new Chunk<>(Collections.singletonList(hashCard));
        
        RuntimeException testException = new RuntimeException("Test exception");
        when(hashCardService.getOrCreate(any(HashCard.class))).thenThrow(testException);
        
        String formattedError = "formatted-error";
        when(looterScrapingErrorHandler.formatErrorItem(
            eq(Constantes.HASH_IMAGE_ITEM), anyString())).thenReturn(formattedError);
        
        // Act
        writer.write(chunk);
        
        // Assert
        verify(looterScrapingErrorHandler).formatErrorItem(
            eq(Constantes.HASH_IMAGE_ITEM), anyString());
        verify(looterScrapingErrorHandler).handle(
            eq(testException),
            eq(Constantes.HASH_IMAGE_WRITER_CONTEXT),
            eq(Constantes.WRITE_ACTION),
            eq(formattedError)
        );
    }
}