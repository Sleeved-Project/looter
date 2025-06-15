package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.repository.iris.HashCardRepository;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.mapper.HashImageMapper;
import com.sleeved.looter.infra.service.IrisApiService;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.HashCardMock;
import com.sleeved.looter.mock.infra.CardImageDTOMock;

@ExtendWith(MockitoExtension.class)
class CardImageDTOToHashCardProcessorTest {

    @Mock
    private IrisApiService irisApiService;

    @Mock
    private LooterScrapingErrorHandler looterScrapingErrorHandler;

    @Mock
    private HashImageMapper hashImageMapper;

    @Mock
    private HashCardRepository hashCardRepository;

    @InjectMocks
    private CardImageDTOToHashCardProcessor processor;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void process_shouldReturnHashCard_whenApiCallSucceeds() throws Exception {
        String imageUrl = "https://example.com/card-image.jpg";
        CardImageDTO cardImageDTO = CardImageDTOMock.createCardImageDTO(imageUrl);

        when(hashCardRepository.findById(anyString())).thenReturn(Optional.empty());
        
        JsonNode mockResponse = objectMapper.createObjectNode()
            .put("hash", "abc123def456");
        
        HashCard expectedHashCard = HashCardMock.createMock("test-card-1", "abc123def456");

        when(irisApiService.fetchHashImage(imageUrl)).thenReturn(mockResponse);
        when(hashImageMapper.toHashCard(cardImageDTO, mockResponse))
            .thenReturn(expectedHashCard);

        HashCard result = processor.process(cardImageDTO);

        assertThat(result)
            .as("Result should not be null")
            .isNotNull();

        assertThat(result.getHash())
            .as("Hash should match expected value")
            .isEqualTo("abc123def456");

        verify(irisApiService).fetchHashImage(imageUrl);
        verify(hashImageMapper).toHashCard(cardImageDTO, mockResponse);
        verifyNoInteractions(looterScrapingErrorHandler);
    }

    @Test
    void process_shouldReturnNull_whenCardAlreadyExists() {
        String imageUrl = "https://example.com/card-image.jpg";
        CardImageDTO cardImageDTO = CardImageDTOMock.createCardImageDTO(imageUrl);
        HashCard existingCard = HashCardMock.createMock(cardImageDTO.getCardId(), "existing-hash");
        
        when(hashCardRepository.findById(cardImageDTO.getCardId())).thenReturn(Optional.of(existingCard));
        
        HashCard result = processor.process(cardImageDTO);
        
        assertThat(result)
            .as("Result should be null when card already exists")
            .isNull();
            
        verify(hashCardRepository).findById(cardImageDTO.getCardId());
        verifyNoInteractions(irisApiService, hashImageMapper, looterScrapingErrorHandler);
    }

    @Test
    void process_shouldReturnNull_whenIrisApiServiceThrowsException() {
        String imageUrl = "https://example.com/card-image.jpg";
        CardImageDTO cardImageDTO = CardImageDTOMock.createCardImageDTO(imageUrl);
        RuntimeException apiException = new RuntimeException("API call failed");
        String formattedItem = "formatted-card-dto-item";

        when(hashCardRepository.findById(anyString())).thenReturn(Optional.empty());

        when(irisApiService.fetchHashImage(imageUrl)).thenThrow(apiException);
        when(looterScrapingErrorHandler.formatErrorItem(
            Constantes.CARD_DTO_ITEM, 
            cardImageDTO.toString()))
            .thenReturn(formattedItem);

        HashCard result = processor.process(cardImageDTO);

        assertThat(result)
            .as("Result should be null when exception occurs")
            .isNull();

        verify(irisApiService).fetchHashImage(imageUrl);
        verify(looterScrapingErrorHandler).formatErrorItem(
            Constantes.CARD_DTO_ITEM, 
            cardImageDTO.toString());
        verify(looterScrapingErrorHandler).handle(
            eq(apiException),
            eq(Constantes.CARD_IMAGE_TO_HASH_IMAGE_PROCESSOR_CONTEXT),
            eq(Constantes.PROCESSOR_ACTION),
            eq(formattedItem));
        verifyNoInteractions(hashImageMapper);
    }

    @Test
    void process_shouldReturnNull_whenMapperThrowsException() {
        String imageUrl = "https://example.com/card-image.jpg";
        CardImageDTO cardImageDTO = CardImageDTOMock.createCardImageDTO(imageUrl);

        when(hashCardRepository.findById(anyString())).thenReturn(Optional.empty());
        
        JsonNode mockResponse = objectMapper.createObjectNode()
            .put("hash", "abc123def456");
        
        RuntimeException mapperException = new RuntimeException("Mapping failed");
        String formattedItem = "formatted-card-dto-item";

        when(irisApiService.fetchHashImage(imageUrl)).thenReturn(mockResponse);
        when(hashImageMapper.toHashCard(cardImageDTO, mockResponse))
            .thenThrow(mapperException);
        when(looterScrapingErrorHandler.formatErrorItem(
            Constantes.CARD_DTO_ITEM, 
            cardImageDTO.toString()))
            .thenReturn(formattedItem);

        HashCard result = processor.process(cardImageDTO);

        assertThat(result)
            .as("Result should be null when mapper throws exception")
            .isNull();

        verify(irisApiService).fetchHashImage(imageUrl);
        verify(hashImageMapper).toHashCard(cardImageDTO, mockResponse);
        verify(looterScrapingErrorHandler).formatErrorItem(
            Constantes.CARD_DTO_ITEM, 
            cardImageDTO.toString());
        verify(looterScrapingErrorHandler).handle(
            eq(mapperException),
            eq(Constantes.CARD_IMAGE_TO_HASH_IMAGE_PROCESSOR_CONTEXT),
            eq(Constantes.PROCESSOR_ACTION),
            eq(formattedItem));
    }
}