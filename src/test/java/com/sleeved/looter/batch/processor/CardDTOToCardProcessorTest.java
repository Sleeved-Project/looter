package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.service.ArtistService;
import com.sleeved.looter.domain.service.LegalitiesService;
import com.sleeved.looter.domain.service.RarityService;
import com.sleeved.looter.domain.service.SetService;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.CardEntitiesProcessedDTO;
import com.sleeved.looter.infra.mapper.ArtistMapper;
import com.sleeved.looter.infra.mapper.CardMapper;
import com.sleeved.looter.infra.mapper.LegalitiesMapper;
import com.sleeved.looter.infra.mapper.RarityMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.ArtistMock;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.RarityMock;
import com.sleeved.looter.mock.domain.SetMock;
import com.sleeved.looter.mock.infra.LegalitiesDTOMock;
import com.sleeved.looter.mock.infra.SetDTOMock;

@ExtendWith(MockitoExtension.class)
public class CardDTOToCardProcessorTest {

  @Mock
  private CardMapper cardMapper;

  @Mock
  private ArtistMapper artistMapper;

  @Mock
  private ArtistService artistService;

  @Mock
  private LegalitiesMapper legalitiesMapper;

  @Mock
  private LegalitiesService legalitiesService;

  @Mock
  private RarityMapper rarityMapper;

  @Mock
  private RarityService rarityService;

  @Mock
  private SetService setService;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private CardDTOToCardProcessor processor;

  private CardDTO cardDTO;
  private Artist artist;
  private Rarity rarity;
  private Legalities legalities;
  private Set set;
  private Card card;

  @BeforeEach
  void setUp() {
    cardDTO = new CardDTO();
    cardDTO.setArtist("Ken Sugimori");
    cardDTO.setRarity("Rare");
    cardDTO.setLegalities(LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal"));
    cardDTO.setSet(SetDTOMock.createMockSetDTO(
        "base1", "Base Set", "Base", 102, 102, "BS",
        "2023/05/15", "2023/05/15 10:30:45",
        "symbol.png", "logo.png"));

    artist = ArtistMock.createMockArtistSavedInDb(1, "Ken Sugimori");
    rarity = RarityMock.createMockRaritySavedInDb(1, "Rare");
    legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS", "symbol.png", "logo.png", legalities);

    card = CardMock.createBasicMockCard("base1-1", "Bulbasaur");
  }

  @Test
  void process_shouldReturnCardEntitiesProcessedDTO_whenProcessSucceeds() {
    Artist artistToFind = ArtistMock.createMockArtist("Ken Sugimori");
    Rarity rarityToFind = RarityMock.createMockRarity("Rare");
    Legalities legalitiesToFind = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    when(artistMapper.toEntity(cardDTO.getArtist())).thenReturn(artistToFind);
    when(artistService.getByName("Ken Sugimori")).thenReturn(artist);

    when(rarityMapper.toEntity(cardDTO.getRarity())).thenReturn(rarityToFind);
    when(rarityService.getByLable("Rare")).thenReturn(rarity);

    when(legalitiesMapper.toEntity(cardDTO.getLegalities())).thenReturn(legalitiesToFind);
    when(legalitiesService.getByStandardExpandedUnlimited(legalitiesToFind)).thenReturn(legalities);

    when(setService.getById("base1")).thenReturn(set);

    when(cardMapper.toEntity(cardDTO, artist, rarity, set, legalities)).thenReturn(card);

    CardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getCard()).isEqualTo(card);

    verify(artistMapper).toEntity(cardDTO.getArtist());
    verify(artistService).getByName("Ken Sugimori");
    verify(rarityMapper).toEntity(cardDTO.getRarity());
    verify(rarityService).getByLable("Rare");
    verify(legalitiesMapper).toEntity(cardDTO.getLegalities());
    verify(legalitiesService).getByStandardExpandedUnlimited(legalitiesToFind);
    verify(setService).getById("base1");
    verify(cardMapper).toEntity(cardDTO, artist, rarity, set, legalities);
  }

  @Test
  void process_shouldReturnNull_whenExceptionIsThrown() {
    RuntimeException exception = new RuntimeException("Test exception");

    when(artistMapper.toEntity(any())).thenThrow(exception);
    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString())).thenReturn("Formatted error");
    doNothing().when(looterScrapingErrorHandler).handle(any(), anyString(), anyString(), anyString());

    CardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNull();

    verify(looterScrapingErrorHandler).formatErrorItem(
        Constantes.CARD_DTO_ITEM,
        cardDTO.toString());
    verify(looterScrapingErrorHandler).handle(
        exception,
        Constantes.CARD_DTO_TO_CARD_ENTITIES_PROCESSOR_CONTEXT,
        Constantes.PROCESSOR_ACTION,
        "Formatted error");
  }

  @Test
  void findArtist_shouldReturnArtist_whenArtistExists() {
    Artist artistToFind = ArtistMock.createMockArtist("Ken Sugimori");

    when(artistMapper.toEntity(cardDTO.getArtist())).thenReturn(artistToFind);
    when(artistService.getByName("Ken Sugimori")).thenReturn(artist);

    Artist result = processor.findArtist(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(artist);

    verify(artistMapper).toEntity(cardDTO.getArtist());
    verify(artistService).getByName("Ken Sugimori");
  }

  @Test
  void findRarity_shouldReturnRarity_whenRarityExists() {
    Rarity rarityToFind = RarityMock.createMockRarity("Rare");

    when(rarityMapper.toEntity(cardDTO.getRarity())).thenReturn(rarityToFind);
    when(rarityService.getByLable("Rare")).thenReturn(rarity);

    Rarity result = processor.findRarity(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(rarity);

    verify(rarityMapper).toEntity(cardDTO.getRarity());
    verify(rarityService).getByLable("Rare");
  }

  @Test
  void findLegalities_shouldReturnLegalities_whenLegalitiesExists() {
    Legalities legalitiesToFind = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    when(legalitiesMapper.toEntity(cardDTO.getLegalities())).thenReturn(legalitiesToFind);
    when(legalitiesService.getByStandardExpandedUnlimited(legalitiesToFind)).thenReturn(legalities);

    Legalities result = processor.findLegalities(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(legalities);

    verify(legalitiesMapper).toEntity(cardDTO.getLegalities());
    verify(legalitiesService).getByStandardExpandedUnlimited(legalitiesToFind);
  }

  @Test
  void findSet_shouldReturnSet_whenSetExists() {
    when(setService.getById("base1")).thenReturn(set);

    Set result = processor.findSet(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(set);

    verify(setService).getById("base1");
  }
}