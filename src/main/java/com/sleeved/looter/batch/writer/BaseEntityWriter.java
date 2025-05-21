package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.*;
import com.sleeved.looter.infra.dto.BaseCardEntitiesProcessedDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BaseEntityWriter implements ItemWriter<BaseCardEntitiesProcessedDTO> {

  private final RarityService rarityService;
  private final ArtistService artistService;
  private final TypeService typeService;
  private final SubtypeService subtypeService;
  private final AbilityService abilityService;
  private final AttackService attackService;
  private final LegalitiesService legalityService;

  public BaseEntityWriter(RarityService rarityService, ArtistService artistService, TypeService typeService,
      SubtypeService subtypeService, AbilityService abilityService, AttackService attackService,
      LegalitiesService legalityService) {
    this.rarityService = rarityService;
    this.artistService = artistService;
    this.typeService = typeService;
    this.subtypeService = subtypeService;
    this.abilityService = abilityService;
    this.attackService = attackService;
    this.legalityService = legalityService;
  }

  @Override
  public void write(Chunk<? extends BaseCardEntitiesProcessedDTO> chunk) throws Exception {
    log.info("WRITER - Chunk size" + chunk.size() + " cards to process...");
    for (BaseCardEntitiesProcessedDTO baseCardEntities : chunk) {
      rarityService.getOrCreate(baseCardEntities.getRarity());
      artistService.getOrCreate(baseCardEntities.getArtist());
      for (Type type : baseCardEntities.getTypes()) {
        typeService.getOrCreate(type);
      }
      for (Subtype subtype : baseCardEntities.getSubtypes()) {
        subtypeService.getOrCreate(subtype);
      }
      for (Ability ability : baseCardEntities.getAbilities()) {
        abilityService.getOrCreate(ability);
      }
      for (Attack attack : baseCardEntities.getAttacks()) {
        attackService.getOrCreate(attack);
      }
      legalityService.getOrCreate(baseCardEntities.getLegalities());
    }
  }

}
