package com.sleeved.looter.batch.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.repository.atlas.CardRepository;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

public class CardToCardImageDTOReaderTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private LooterScrapingErrorHandler looterScrapingErrorHandler;

    private CardToCardImageDTOReader reader;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        Card card1 = new Card();
        card1.setId("card-1");
        card1.setImageLarge("http://example.com/card1.jpg");
        
        Card card2 = new Card();
        card2.setId("card-2");
        card2.setImageLarge("http://example.com/card2.jpg");
        
        Card cardWithoutImage = new Card();
        cardWithoutImage.setId("card-3");
        cardWithoutImage.setImageLarge(null);
        
        when(cardRepository.findByImageLargeIsNotNull())
            .thenReturn(Arrays.asList(card1, card2, cardWithoutImage));
            
        reader = new CardToCardImageDTOReader(cardRepository, looterScrapingErrorHandler);
    }

    /**
     * Tests successful reading of cards with valid image URLs.
     */
    @Test
    public void testReadSuccess() throws Exception {
        CardImageDTO firstResult = reader.read();
        assertEquals("card-1", firstResult.getCardId());
        assertEquals("http://example.com/card1.jpg", firstResult.getImageUrl());
        
        CardImageDTO secondResult = reader.read();
        assertEquals("card-2", secondResult.getCardId());
        assertEquals("http://example.com/card2.jpg", secondResult.getImageUrl());
        
        CardImageDTO thirdResult = reader.read();
        assertNull(thirdResult);
        
        CardImageDTO fourthResult = reader.read();
        assertNull(fourthResult);
    }
    
    /**
     * Tests reading behavior when repository returns empty list.
     */
    @Test
    public void testReadWithEmptyList() throws Exception {
        when(cardRepository.findByImageLargeIsNotNull())
            .thenReturn(Collections.emptyList());
            
        reader = new CardToCardImageDTOReader(cardRepository, looterScrapingErrorHandler);
        
        assertNull(reader.read());
    }
    
    /**
     * Tests error handling when repository throws exception during initialization.
     */
    @Test
    public void testReadWithException() throws Exception {
        when(cardRepository.findByImageLargeIsNotNull())
            .thenThrow(new RuntimeException("Database error"));
        
        doNothing().when(looterScrapingErrorHandler).handle(any(), anyString(), anyString(), anyString());
        
        reader = new CardToCardImageDTOReader(cardRepository, looterScrapingErrorHandler);
        
        assertNull(reader.read());
        
        verify(looterScrapingErrorHandler, times(1)).handle(any(), anyString(), anyString(), anyString());
    }
}