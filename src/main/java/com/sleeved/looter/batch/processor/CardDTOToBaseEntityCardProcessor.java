package com.sleeved.looter.batch.processor;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.infra.dto.BaseCardEntitiesProcessedDTO;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.mapper.AbilityMapper;
import com.sleeved.looter.infra.mapper.ArtistMapper;
import com.sleeved.looter.infra.mapper.AttackMapper;
import com.sleeved.looter.infra.mapper.LegalitiesMapper;
import com.sleeved.looter.infra.mapper.RarityMapper;
import com.sleeved.looter.infra.mapper.SubtypeMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardDTOToBaseEntityCardProcessor implements ItemProcessor<CardDTO, BaseCardEntitiesProcessedDTO> {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final RarityMapper rarityMapper;
  private final ArtistMapper artistMapper;
  private final TypeMapper typeMapper;
  private final SubtypeMapper subtypeMapper;
  private final AbilityMapper abilityMapper;
  private final AttackMapper attackMapper;
  private final LegalitiesMapper legalitiesMapper;

  public CardDTOToBaseEntityCardProcessor(RarityMapper rarityMapper, ArtistMapper artistMapper, TypeMapper typeMapper,
      SubtypeMapper subtypeMapper, AbilityMapper abilityMapper, AttackMapper attackMapper,
      LegalitiesMapper legalitiesMapper, LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.rarityMapper = rarityMapper;
    this.artistMapper = artistMapper;
    this.typeMapper = typeMapper;
    this.subtypeMapper = subtypeMapper;
    this.abilityMapper = abilityMapper;
    this.attackMapper = attackMapper;
    this.legalitiesMapper = legalitiesMapper;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public BaseCardEntitiesProcessedDTO process(CardDTO item) {
    try {
      BaseCardEntitiesProcessedDTO baseCardEntitiesProcessedDTO = new BaseCardEntitiesProcessedDTO();

      Rarity rarity = rarityMapper.toEntity(item.getRarity());
      Artist artist = artistMapper.toEntity(item.getArtist());
      List<Type> types = typeMapper.toListEntity(item.getTypes());
      List<Subtype> subtypes = subtypeMapper.toListEntity(item.getSubtypes());
      List<Ability> abilities = abilityMapper.toListEntity(item.getAbilities());
      List<Attack> attacks = attackMapper.toListEntity(item.getAttacks());
      Legalities legalities = legalitiesMapper.toEntity(item.getLegalities());

      baseCardEntitiesProcessedDTO.setRarity(rarity);
      baseCardEntitiesProcessedDTO.setArtist(artist);
      baseCardEntitiesProcessedDTO.setTypes(types);
      baseCardEntitiesProcessedDTO.setSubtypes(subtypes);
      baseCardEntitiesProcessedDTO.setAbilities(abilities);
      baseCardEntitiesProcessedDTO.setAttacks(attacks);
      baseCardEntitiesProcessedDTO.setLegalities(legalities);

      return baseCardEntitiesProcessedDTO;
    } catch (Exception e) {
      looterScrapingErrorHandler.handle(e, Constantes.CARD_DTO_TO_BASE_ENTITY_PROCESSOR_CONTEXT,
          Constantes.PROCESSOR_ACTION,
          Constantes.CARD_DTO_ITEM);
      return null;
    }

  }

}
