package com.sleeved.looter.batch.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.AttackService;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.AttackDTO;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.CostAttackEntitiesProcessedDTO;
import com.sleeved.looter.infra.mapper.*;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardDTOToCostAttackCardProcessor
    implements ItemProcessor<CardDTO, CostAttackEntitiesProcessedDTO> {

  private final AttackService attackService;
  private final AttackMapper attackMapper;
  private final CostAttackMapper costAttackMapper;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final TypeMapper typeMapper;
  private final TypeService typeService;

  public CardDTOToCostAttackCardProcessor(
      AttackMapper attackMapper, AttackService attackService,
      TypeMapper typeMapper,
      TypeService typeService,
      LooterScrapingErrorHandler looterScrapingErrorHandler, CostAttackMapper costAttackMapper) {
    this.attackMapper = attackMapper;
    this.attackService = attackService;
    this.typeMapper = typeMapper;
    this.typeService = typeService;
    this.costAttackMapper = costAttackMapper;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public CostAttackEntitiesProcessedDTO process(CardDTO item) {
    try {
      List<CostAttack> costAttacks = processCostAttacks(item.getAttacks());
      CostAttackEntitiesProcessedDTO costAttackEntitiesProcessedDTO = new CostAttackEntitiesProcessedDTO();
      costAttackEntitiesProcessedDTO.setCostAttacks(costAttacks);
      return costAttackEntitiesProcessedDTO;
    } catch (Exception e) {
      String formatedItem = looterScrapingErrorHandler.formatErrorItem(
          Constantes.CARD_DTO_ITEM,
          item.toString());
      looterScrapingErrorHandler.handle(e,
          Constantes.CARD_DTO_TO_COST_ATTACK_CARD_ENTITIES_PROCESSOR_CONTEXT,
          Constantes.PROCESSOR_ACTION,
          formatedItem);
      return null;
    }
  }

  protected List<CostAttack> processCostAttacks(List<AttackDTO> attacksDTO) {
    if (attacksDTO == null) {
      return new ArrayList<>();
    }

    List<CostAttack> costAttacks = new ArrayList<>();

    for (AttackDTO attackDTO : attacksDTO) {
      Attack attack = findAttackFromDTO(attackDTO);
      if (isAttackFree(attack)) {
        CostAttack freeCostAttack = costAttackMapper.toFreeAttackEntity(attack);
        costAttacks.add(freeCostAttack);
      } else {
        Map<Type, Integer> typeCostsMap = findAndAggregateTypeCosts(attackDTO);
        List<CostAttack> attackCosts = createCostAttacksFromTypeMap(attack, typeCostsMap);
        costAttacks.addAll(attackCosts);
      }
    }

    return costAttacks;
  }

  protected boolean isAttackFree(Attack attack) {
    return attack.getConvertedEnergyCost() == 0;
  }

  protected Attack findAttackFromDTO(AttackDTO attackDTO) {
    Attack attackToFind = attackMapper.toEntity(attackDTO);
    return attackService.getByNameAndDamageAndConvertedEnegyCostAndText(attackToFind);
  }

  protected Map<Type, Integer> findAndAggregateTypeCosts(AttackDTO attackDTO) {
    List<Type> typesToFind = typeMapper.toListEntity(attackDTO.getCost());
    Map<Type, Integer> typeCostsMap = new HashMap<>();

    for (Type typeToFind : typesToFind) {
      Type typeFound = typeService.getByLabel(typeToFind);
      typeCostsMap.merge(typeFound, 1, Integer::sum);
    }

    return typeCostsMap;
  }

  protected List<CostAttack> createCostAttacksFromTypeMap(Attack attack, Map<Type, Integer> typeCostsMap) {
    List<CostAttack> costAttacks = new ArrayList<>();

    for (Map.Entry<Type, Integer> entry : typeCostsMap.entrySet()) {
      Type type = entry.getKey();
      Integer cost = entry.getValue();

      CostAttack costAttack = costAttackMapper.toEntity(attack, type, cost);
      costAttacks.add(costAttack);
    }

    return costAttacks;
  }

}
