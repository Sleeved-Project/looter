package com.sleeved.looter.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.service.*;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.CardEntitiesProcessedDTO;
import com.sleeved.looter.infra.mapper.*;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardDTOToCardProcessor
    implements ItemProcessor<CardDTO, CardEntitiesProcessedDTO> {

  private final CardMapper cardMapper;

  private final ArtistMapper artistMapper;
  private final ArtistService artistService;
  private final LegalitiesMapper legalitiesMapper;
  private final LegalitiesService legalitiesService;
  private final RarityMapper rarityMapper;
  private final RarityService rarityService;
  private final SetService setService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public CardDTOToCardProcessor(
      ArtistMapper artistMapper, ArtistService artistService, CardMapper cardMapper, LegalitiesMapper legalitiesMapper,
      LegalitiesService legalitiesService, RarityMapper rarityMapper, RarityService rarityService,
      SetService setService, LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.artistMapper = artistMapper;
    this.artistService = artistService;
    this.cardMapper = cardMapper;
    this.legalitiesMapper = legalitiesMapper;
    this.legalitiesService = legalitiesService;
    this.rarityMapper = rarityMapper;
    this.rarityService = rarityService;
    this.setService = setService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public CardEntitiesProcessedDTO process(CardDTO item) {
    try {
      Artist cardArtistFound = findArtist(item);
      Rarity cardRarityFound = findRarity(item);
      Legalities cardLegalitiesFound = findLegalities(item);
      Set cardSetFound = findSet(item);

      CardEntitiesProcessedDTO cardEntitiesProcessedDTO = new CardEntitiesProcessedDTO();
      Card card = cardMapper.toEntity(item, cardArtistFound, cardRarityFound, cardSetFound, cardLegalitiesFound);

      cardEntitiesProcessedDTO.setCard(card);
      return cardEntitiesProcessedDTO;
    } catch (Exception e) {
      String formatedItem = looterScrapingErrorHandler.formatErrorItem(
          Constantes.CARD_DTO_ITEM,
          item.toString());
      looterScrapingErrorHandler.handle(e,
          Constantes.CARD_DTO_TO_CARD_ENTITIES_PROCESSOR_CONTEXT,
          Constantes.PROCESSOR_ACTION,
          formatedItem);
      return null;
    }
  }

  protected Artist findArtist(CardDTO cardDTO) {
    Artist cardArtistToFind = artistMapper.toEntity(cardDTO.getArtist());
    return artistService.getByName(cardArtistToFind.getName());
  }

  protected Rarity findRarity(CardDTO cardDTO) {
    Rarity cardRarityToFind = rarityMapper.toEntity(cardDTO.getRarity());
    return rarityService.getByLable(cardRarityToFind.getLabel());
  }

  protected Legalities findLegalities(CardDTO cardDTO) {
    Legalities cardLegalitiesToFind = legalitiesMapper.toEntity(cardDTO.getLegalities());
    return legalitiesService.getByStandardExpandedUnlimited(cardLegalitiesToFind);
  }

  protected Set findSet(CardDTO cardDTO) {
    return setService.getById(cardDTO.getSet().getId());
  }
}
