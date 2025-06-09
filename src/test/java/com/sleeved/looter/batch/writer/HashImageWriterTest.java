package com.sleeved.looter.batch.writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.service.HashCardService;
import com.sleeved.looter.infra.dto.HashImageDTO;
import com.sleeved.looter.infra.mapper.HashCardMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

@ExtendWith(MockitoExtension.class)
class HashImageWriterTest {

    @Mock
    private HashCardService hashCardService;

    @Mock
    private HashCardMapper hashCardMapper;

    @Mock
    private LooterScrapingErrorHandler looterScrapingErrorHandler;

    @InjectMocks
    private HashImageWriter writer;

    @Test
    void write_shouldProcessEmptyChunk() throws Exception {
        Chunk<HashImageDTO> chunk = new Chunk<>(Collections.emptyList());

        writer.write(chunk);

        verifyNoInteractions(hashCardService, hashCardMapper, looterScrapingErrorHandler);
    }

    @Test
    void write_shouldProcessSingleValidHashImageDTO() throws Exception {
        HashImageDTO dto = new HashImageDTO();
        dto.setId("card-123");
        dto.setHash("a1b2c3d4e5f6");

        HashCard hashCard = new HashCard();
        hashCard.setId("card-123");
        hashCard.setHash("a1b2c3d4e5f6");

        Chunk<HashImageDTO> chunk = new Chunk<>(List.of(dto));

        when(hashCardMapper.toEntity(dto)).thenReturn(hashCard);
        when(hashCardService.getOrCreate(hashCard)).thenReturn(hashCard);

        writer.write(chunk);

        verify(hashCardMapper).toEntity(dto);
        verify(hashCardService).getOrCreate(hashCard);
        verifyNoInteractions(looterScrapingErrorHandler);
    }

    @Test
    void write_shouldSkipNullHashImageDTO() throws Exception {
        Chunk<HashImageDTO> chunk = new Chunk<>(Arrays.asList((HashImageDTO) null));

        writer.write(chunk);

        verify(hashCardMapper, never()).toEntity(any());
        verify(hashCardService, never()).getOrCreate(any());
        verifyNoInteractions(looterScrapingErrorHandler);
    }

    @Test
    void write_shouldSkipHashImageDTOWithNullHash() throws Exception {
        HashImageDTO dto = new HashImageDTO();
        dto.setId("card-123");
        dto.setHash(null);
        
        Chunk<HashImageDTO> chunk = new Chunk<>(List.of(dto));

        writer.write(chunk);

        verify(hashCardMapper, never()).toEntity(any());
        verify(hashCardService, never()).getOrCreate(any());
        verifyNoInteractions(looterScrapingErrorHandler);
    }

    @Test
    void write_shouldProcessMultipleValidHashImageDTOs() throws Exception {
        HashImageDTO dto1 = new HashImageDTO();
        dto1.setId("card-123");
        dto1.setHash("a1b2c3d4e5f6");

        HashImageDTO dto2 = new HashImageDTO();
        dto2.setId("card-456");
        dto2.setHash("f6e5d4c3b2a1");

        HashCard hashCard1 = new HashCard();
        hashCard1.setId("card-123");
        hashCard1.setHash("a1b2c3d4e5f6");

        HashCard hashCard2 = new HashCard();
        hashCard2.setId("card-456");
        hashCard2.setHash("f6e5d4c3b2a1");

        Chunk<HashImageDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

        when(hashCardMapper.toEntity(dto1)).thenReturn(hashCard1);
        when(hashCardMapper.toEntity(dto2)).thenReturn(hashCard2);
        when(hashCardService.getOrCreate(hashCard1)).thenReturn(hashCard1);
        when(hashCardService.getOrCreate(hashCard2)).thenReturn(hashCard2);

        writer.write(chunk);

        verify(hashCardMapper).toEntity(dto1);
        verify(hashCardMapper).toEntity(dto2);
        verify(hashCardService).getOrCreate(hashCard1);
        verify(hashCardService).getOrCreate(hashCard2);
        verifyNoInteractions(looterScrapingErrorHandler);
    }

    @Test
    void write_shouldHandleExceptionWithErrorHandler() throws Exception {
        HashImageDTO dto = new HashImageDTO();
        dto.setId("card-123");
        dto.setHash("a1b2c3d4e5f6");

        HashCard hashCard = new HashCard();
        hashCard.setId("card-123");
        hashCard.setHash("a1b2c3d4e5f6");

        Exception serviceException = new RuntimeException("Service error");

        Chunk<HashImageDTO> chunk = new Chunk<>(List.of(dto));

        when(hashCardMapper.toEntity(dto)).thenReturn(hashCard);
        when(hashCardService.getOrCreate(hashCard)).thenThrow(serviceException);
        when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
            .thenReturn("Formatted error item");

        writer.write(chunk);

        verify(hashCardMapper).toEntity(dto);
        verify(hashCardService).getOrCreate(hashCard);
        
        // Note: Currently your implementation uses CARD_ENTITIES_ITEM instead of a dedicated constant
        verify(looterScrapingErrorHandler).formatErrorItem(
            eq(Constantes.CARD_ENTITIES_ITEM),
            anyString());
            
        verify(looterScrapingErrorHandler).handle(
            eq(serviceException),
            eq(Constantes.HASH_IMAGE_WRITER_CONTEXT),
            eq(Constantes.WRITE_ACTION),
            anyString());
    }

    @Test
    void write_shouldContinueProcessingAfterException() throws Exception {
        HashImageDTO dto1 = new HashImageDTO();
        dto1.setId("card-123");
        dto1.setHash("a1b2c3d4e5f6");

        HashImageDTO dto2 = new HashImageDTO();
        dto2.setId("card-456");
        dto2.setHash("f6e5d4c3b2a1");

        HashCard hashCard1 = new HashCard();
        hashCard1.setId("card-123");
        hashCard1.setHash("a1b2c3d4e5f6");

        HashCard hashCard2 = new HashCard();
        hashCard2.setId("card-456");
        hashCard2.setHash("f6e5d4c3b2a1");

        Exception serviceException = new RuntimeException("Service error");

        Chunk<HashImageDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

        when(hashCardMapper.toEntity(dto1)).thenReturn(hashCard1);
        when(hashCardMapper.toEntity(dto2)).thenReturn(hashCard2);
        when(hashCardService.getOrCreate(hashCard1)).thenThrow(serviceException);
        when(hashCardService.getOrCreate(hashCard2)).thenReturn(hashCard2);
        when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
            .thenReturn("Formatted error item");

        writer.write(chunk);

        verify(hashCardMapper).toEntity(dto1);
        verify(hashCardMapper).toEntity(dto2);
        verify(hashCardService).getOrCreate(hashCard1);
        verify(hashCardService).getOrCreate(hashCard2);
        verify(looterScrapingErrorHandler, times(1)).handle(
            any(Exception.class),
            anyString(),
            anyString(),
            anyString());
    }
    
    @Test
    void write_shouldHandleMixedValidAndInvalidItems() throws Exception {
        HashImageDTO validDto = new HashImageDTO();
        validDto.setId("card-123");
        validDto.setHash("a1b2c3d4e5f6");
        
        HashImageDTO nullHashDto = new HashImageDTO();
        nullHashDto.setId("card-456");
        nullHashDto.setHash(null);
        
        HashCard hashCard = new HashCard();
        hashCard.setId("card-123");
        hashCard.setHash("a1b2c3d4e5f6");

        Chunk<HashImageDTO> chunk = new Chunk<>(Arrays.asList(validDto, nullHashDto, null));

        when(hashCardMapper.toEntity(validDto)).thenReturn(hashCard);
        when(hashCardService.getOrCreate(hashCard)).thenReturn(hashCard);

        writer.write(chunk);

        verify(hashCardMapper, times(1)).toEntity(any());
        verify(hashCardService, times(1)).getOrCreate(any());
        verifyNoInteractions(looterScrapingErrorHandler);
    }
}