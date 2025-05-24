package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.*;
import com.sleeved.looter.infra.dto.BaseCardEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BaseEntityWriter implements ItemWriter<BaseCardEntitiesProcessedDTO> {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  private final RarityService rarityService;
  private final ArtistService artistService;
  private final TypeService typeService;
  private final SubtypeService subtypeService;
  private final AbilityService abilityService;
  private final AttackService attackService;
  private final LegalitiesService legalityService;

  public BaseEntityWriter(RarityService rarityService, ArtistService artistService, TypeService typeService,
      SubtypeService subtypeService, AbilityService abilityService, AttackService attackService,
      LegalitiesService legalityService, LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.rarityService = rarityService;
    this.artistService = artistService;
    this.typeService = typeService;
    this.subtypeService = subtypeService;
    this.abilityService = abilityService;
    this.attackService = attackService;
    this.legalityService = legalityService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends BaseCardEntitiesProcessedDTO> chunk) throws Exception {
    for (BaseCardEntitiesProcessedDTO baseCardEntities : chunk) {
      try {
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
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.BASE_CARD_ENTITIES_ITEM,
            baseCardEntities.toString());
        looterScrapingErrorHandler.handle(e, Constantes.BASE_ENTITY_WRITER_CONTEXT, Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}
