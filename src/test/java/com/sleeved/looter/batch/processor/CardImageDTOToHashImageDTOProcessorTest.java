package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private HashImageMapper mapper;
    @Mock
    private HashCardRepository repo;
    @Mock
    private LooterScrapingErrorHandler errorHandler;

    @InjectMocks
    private CardImageDTOToHashCardProcessor processor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void process_ShouldMapHashCard_WhenCardIsNew() throws Exception {
        CardImageDTO dto = CardImageDTOMock.createCardImageDTO("http://img.jpg");
        JsonNode response = objectMapper.createObjectNode().put("hash", "abc");
        HashCard expected = HashCardMock.createMock(dto.getCardId(), "abc");

        when(repo.existsById(dto.getCardId())).thenReturn(false);
        when(irisApiService.fetchHashImage(dto.getImageUrl())).thenReturn(response);
        when(mapper.toHashCard(dto, response)).thenReturn(expected);

        HashCard result = processor.process(dto);

        assertThat(result).isEqualTo(expected);
        verify(repo).existsById(dto.getCardId());
        verify(irisApiService).fetchHashImage(dto.getImageUrl());
        verify(mapper).toHashCard(dto, response);
    }

    @Test
    void process_ShouldReturnNull_WhenCardAlreadyExists() {
        CardImageDTO dto = CardImageDTOMock.createCardImageDTO("http://img.jpg");
        when(repo.existsById(dto.getCardId())).thenReturn(true);

        HashCard result = processor.process(dto);

        assertThat(result).isNull();
        verify(repo).existsById(dto.getCardId());
        verifyNoInteractions(irisApiService);
        verifyNoInteractions(mapper);
    }
}
