package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.dto.HashImageDTO;
import com.sleeved.looter.infra.service.IrisApiService;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

@ExtendWith(MockitoExtension.class)
class CardImageDTOToHashImageDTOProcessorTest {
    @Mock
    private IrisApiService irisApiService;
    
    @Mock
    private LooterScrapingErrorHandler looterScrapingErrorHandler;
    
    @InjectMocks
    private CardImageDTOToHashImageDTOProcessor processor;
    
    @Test
    void process_shouldReturnHashImageDTO_whenHashIsGenerated() throws Exception {
        String cardId = "card-123";
        String imageUrl = "http://example.com/card123.jpg";
        String generatedHash = "a1b2c3d4e5f6g7h8i9j0";
        
        CardImageDTO cardImageDTO = new CardImageDTO();
        cardImageDTO.setCardId(cardId);
        cardImageDTO.setImageUrl(imageUrl);
        
        when(irisApiService.fetchHashImage(imageUrl)).thenReturn(generatedHash);
        
        HashImageDTO result = processor.process(cardImageDTO);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(cardId);
        assertThat(result.getHash()).isEqualTo(generatedHash);
        verifyNoInteractions(looterScrapingErrorHandler);
    }
    
    @Test
    void process_shouldReturnNull_whenHashIsNull() throws Exception {
        String cardId = "card-123";
        String imageUrl = "http://example.com/card123.jpg";
        
        CardImageDTO cardImageDTO = new CardImageDTO();
        cardImageDTO.setCardId(cardId);
        cardImageDTO.setImageUrl(imageUrl);
        
        when(irisApiService.fetchHashImage(imageUrl)).thenReturn(null);
        
        HashImageDTO result = processor.process(cardImageDTO);
        
        assertThat(result).isNull();
        verifyNoInteractions(looterScrapingErrorHandler);
    }
    
    @Test
    void process_shouldReturnNull_whenHashIsEmpty() throws Exception {
        String cardId = "card-123";
        String imageUrl = "http://example.com/card123.jpg";
        
        CardImageDTO cardImageDTO = new CardImageDTO();
        cardImageDTO.setCardId(cardId);
        cardImageDTO.setImageUrl(imageUrl);
        
        when(irisApiService.fetchHashImage(imageUrl)).thenReturn("");
        
        HashImageDTO result = processor.process(cardImageDTO);
        
        assertThat(result).isNull();
        verifyNoInteractions(looterScrapingErrorHandler);
    }
    
    @Test
    void process_shouldHandleExceptionAndReturnNull_whenErrorOccurs() throws Exception {
        String cardId = "card-123";
        String imageUrl = "http://example.com/card123.jpg";
        
        CardImageDTO cardImageDTO = new CardImageDTO();
        cardImageDTO.setCardId(cardId);
        cardImageDTO.setImageUrl(imageUrl);
        
        RuntimeException apiException = new RuntimeException("API error");
        when(irisApiService.fetchHashImage(imageUrl)).thenThrow(apiException);
        
        when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString())).thenReturn("Formatted error");
        
        HashImageDTO result = processor.process(cardImageDTO);
        
        assertThat(result).isNull();
        
        verify(looterScrapingErrorHandler).formatErrorItem(
            eq(Constantes.CARD_DTO_ITEM), 
            anyString()
        );
        
        verify(looterScrapingErrorHandler).handle(
            eq(apiException),
            eq(Constantes.CARD_IMAGE_TO_HASH_IMAGE_PROCESSOR_CONTEXT),
            eq(Constantes.PROCESSOR_ACTION),
            anyString()
        );
    }
    
    @Test
    void process_shouldHandleNullInputDTO() throws Exception {
        assertThat(processor.process(null)).isNull();
        verifyNoInteractions(irisApiService, looterScrapingErrorHandler);
    }

}