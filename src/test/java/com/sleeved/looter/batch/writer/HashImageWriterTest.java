package com.sleeved.looter.batch.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private LooterScrapingErrorHandler errorHandler;

    private HashImageWriter writer;

    @BeforeEach
    void setUp() {
        writer = new HashImageWriter(hashCardService, errorHandler);
    }

    @Test
    void write_ShouldSaveValidCards() throws Exception {
        HashCard c1 = HashCardMock.createMock("c1", "h1");
        HashCard c2 = HashCardMock.createMock("c2", "h2");
        Chunk<HashCard> chunk = new Chunk<>(List.of(c1, c2));

        writer.write(chunk);

        verify(hashCardService).saveAll(List.of(c1, c2));
        verifyNoInteractions(errorHandler);
    }

    @Test
    void write_ShouldFallbackToIndividualSave_WhenBatchFails() throws Exception {
        HashCard c1 = HashCardMock.createMock("c1", "h1");
        HashCard c2 = HashCardMock.createMock("c2", "h2");
        Chunk<HashCard> chunk = new Chunk<>(List.of(c1, c2));

        when(hashCardService.saveAll(anyList())).thenThrow(new RuntimeException("batch fail"));

        writer.write(chunk);

        verify(hashCardService).saveAll(anyList());
        verify(hashCardService).save(c1);
        verify(hashCardService).save(c2);
    }
}
