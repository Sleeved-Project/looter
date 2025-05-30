package com.sleeved.looter.batch.writer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.service.CardService;
import com.sleeved.looter.infra.dto.CardEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.ArtistMock;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.RarityMock;
import com.sleeved.looter.mock.domain.SetMock;

@ExtendWith(MockitoExtension.class)
class CardWriterTest {

  @Mock
  private CardService cardService;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private CardWriter writer;

  @Test
  void write_shouldProcessEmptyChunk() throws Exception {
    Chunk<CardEntitiesProcessedDTO> chunk = new Chunk<>(Collections.emptyList());

    writer.write(chunk);

    verifyNoInteractions(cardService, looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessSingleCard() throws Exception {
    Artist artist = ArtistMock.createMockArtistSavedInDb(1, "Ken Sugimori");
    Rarity rarity = RarityMock.createMockRaritySavedInDb(1, "Rare");
    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS", "symbol.png", "logo.png", legalities);

    Card card = CardMock.createMockCard(
        "base1-4",
        "Charizard",
        "Pokémon",
        "76",
        "120",
        "Charizard GX, Charizard V",
        3,
        "4",
        "https://images.pokemontcg.io/base1/4_large.png",
        "https://images.pokemontcg.io/base1/4_small.png",
        "Spits fire that is hot enough to melt boulders.",
        "6",
        artist,
        rarity,
        set,
        legalities);

    CardEntitiesProcessedDTO dto = new CardEntitiesProcessedDTO();
    dto.setCard(card);

    Chunk<CardEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    when(cardService.getOrCreate(card)).thenReturn(card);

    writer.write(chunk);

    verify(cardService).getOrCreate(card);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessMultipleCards() throws Exception {
    Artist artist1 = ArtistMock.createMockArtistSavedInDb(1, "Ken Sugimori");
    Rarity rarity1 = RarityMock.createMockRaritySavedInDb(1, "Rare");
    Legalities legalities1 = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set1 = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS", "symbol.png", "logo.png",
        legalities1);

    Card card1 = CardMock.createMockCard(
        "base1-4",
        "Charizard",
        "Pokémon",
        "76",
        "120",
        "Charizard GX, Charizard V",
        3,
        "4",
        "https://images.pokemontcg.io/base1/4_large.png",
        "https://images.pokemontcg.io/base1/4_small.png",
        "Spits fire that is hot enough to melt boulders.",
        "6",
        artist1,
        rarity1,
        set1,
        legalities1);

    Artist artist2 = ArtistMock.createMockArtistSavedInDb(2, "Mitsuhiro Arita");
    Rarity rarity2 = RarityMock.createMockRaritySavedInDb(2, "Common");
    Legalities legalities2 = LegalitiesMock.createMockLegalitiesSavedInDb(2, "Legal", "Legal", "Legal");
    Set set2 = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS", "symbol.png", "logo.png",
        legalities2);

    Card card2 = CardMock.createMockCard(
        "base1-25",
        "Pikachu",
        "Pokémon",
        null,
        "60",
        null,
        1,
        "25",
        "https://images.pokemontcg.io/base1/25_large.png",
        "https://images.pokemontcg.io/base1/25_small.png",
        "When several of these Pokémon gather, their electricity could build and cause lightning storms.",
        "25",
        artist2,
        rarity2,
        set2,
        legalities2);

    CardEntitiesProcessedDTO dto1 = new CardEntitiesProcessedDTO();
    dto1.setCard(card1);

    CardEntitiesProcessedDTO dto2 = new CardEntitiesProcessedDTO();
    dto2.setCard(card2);

    Chunk<CardEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

    when(cardService.getOrCreate(card1)).thenReturn(card1);
    when(cardService.getOrCreate(card2)).thenReturn(card2);

    writer.write(chunk);

    verify(cardService).getOrCreate(card1);
    verify(cardService).getOrCreate(card2);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldHandleExceptionDuringSingleCardProcessing() throws Exception {
    Card card = CardMock.createBasicMockCard("base1-4", "Charizard");

    CardEntitiesProcessedDTO dto = new CardEntitiesProcessedDTO();
    dto.setCard(card);

    Chunk<CardEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    RuntimeException exception = new RuntimeException("Error processing card");
    when(cardService.getOrCreate(card)).thenThrow(exception);
    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    writer.write(chunk);

    verify(cardService).getOrCreate(card);
    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.CARD_ENTITIES_ITEM),
        anyString());
    verify(looterScrapingErrorHandler).handle(
        eq(exception),
        eq(Constantes.CARD_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());
  }

  @Test
  void write_shouldProcessRemainingCardsWhenOneCardFails() throws Exception {
    Card card1 = CardMock.createBasicMockCard("base1-4", "Charizard");
    Card card2 = CardMock.createBasicMockCard("base1-25", "Pikachu");
    Card card3 = CardMock.createBasicMockCard("base1-2", "Blastoise");

    CardEntitiesProcessedDTO dto1 = new CardEntitiesProcessedDTO();
    dto1.setCard(card1);

    CardEntitiesProcessedDTO dto2 = new CardEntitiesProcessedDTO();
    dto2.setCard(card2);

    CardEntitiesProcessedDTO dto3 = new CardEntitiesProcessedDTO();
    dto3.setCard(card3);

    Chunk<CardEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2, dto3));

    when(cardService.getOrCreate(card1)).thenReturn(card1);
    RuntimeException exception = new RuntimeException("Error processing card");
    when(cardService.getOrCreate(card2)).thenThrow(exception);
    when(cardService.getOrCreate(card3)).thenReturn(card3);

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    writer.write(chunk);

    verify(cardService).getOrCreate(card1);
    verify(cardService).getOrCreate(card2);
    verify(cardService).getOrCreate(card3);
    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.CARD_ENTITIES_ITEM),
        anyString());
    verify(looterScrapingErrorHandler).handle(
        eq(exception),
        eq(Constantes.CARD_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());
  }

  @Test
  void write_shouldHandleNullCard() throws Exception {
    CardEntitiesProcessedDTO dto = new CardEntitiesProcessedDTO();
    dto.setCard(null);
    Chunk<CardEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    when(cardService.getOrCreate(null)).thenReturn(null);
    writer.write(chunk);

    verify(cardService).getOrCreate(null);
    verifyNoInteractions(looterScrapingErrorHandler);
  }
}