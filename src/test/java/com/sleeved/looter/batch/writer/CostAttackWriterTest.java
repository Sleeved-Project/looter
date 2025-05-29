package com.sleeved.looter.batch.writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.CostAttackService;
import com.sleeved.looter.infra.dto.CostAttackEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.CostAttackMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
class CostAttackWriterTest {

  @Mock
  private CostAttackService costAttackService;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private CostAttackWriter writer;

  @Test
  void write_shouldProcessEmptyChunk() throws Exception {
    Chunk<CostAttackEntitiesProcessedDTO> chunk = new Chunk<>(Collections.emptyList());

    writer.write(chunk);

    verifyNoInteractions(costAttackService, looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessSingleItemWithNoCostAttacks() throws Exception {
    CostAttackEntitiesProcessedDTO dto = new CostAttackEntitiesProcessedDTO();
    dto.setCostAttacks(Collections.emptyList());
    Chunk<CostAttackEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto));

    writer.write(chunk);

    verifyNoInteractions(costAttackService, looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessSingleRegularCostAttack() throws Exception {
    Attack attack = AttackMock.createMockAttackSavedInDb(1, "Flamethrower", "90", 3, "Discard 1 Fire Energy");
    Type type = TypeMock.createMockTypeSavedInDb(1, "Fire");
    CostAttack costAttack = CostAttackMock.createMockCostAttackSavedInDb(1, attack, type, 2, false);

    CostAttackEntitiesProcessedDTO dto = new CostAttackEntitiesProcessedDTO();
    dto.setCostAttacks(List.of(costAttack));

    Chunk<CostAttackEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    when(costAttackService.getOrCreate(costAttack)).thenReturn(costAttack);

    writer.write(chunk);

    verify(costAttackService).getOrCreate(costAttack);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessSingleFreeCostAttack() throws Exception {
    Attack attack = AttackMock.createMockAttackSavedInDb(2, "Zero Cost", "10", 0, "Free attack");
    CostAttack freeCostAttack = CostAttackMock.createMockFreeCostAttackSavedInDb(2, attack);

    CostAttackEntitiesProcessedDTO dto = new CostAttackEntitiesProcessedDTO();
    dto.setCostAttacks(List.of(freeCostAttack));

    Chunk<CostAttackEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    when(costAttackService.getOrCreate(freeCostAttack)).thenReturn(freeCostAttack);

    writer.write(chunk);

    verify(costAttackService).getOrCreate(freeCostAttack);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessMultipleCostAttacksForOneItem() throws Exception {
    Attack attack1 = AttackMock.createMockAttackSavedInDb(3, "Hydro Pump", "60+", 3, "Extra damage for Water Energy");
    Type waterType = TypeMock.createMockTypeSavedInDb(2, "Water");
    CostAttack costAttack1 = CostAttackMock.createMockCostAttackSavedInDb(3, attack1, waterType, 2, false);

    Attack attack2 = AttackMock.createMockAttackSavedInDb(4, "Psychic", "50+", 2, "Extra damage for Psychic Energy");
    Type psychicType = TypeMock.createMockTypeSavedInDb(3, "Psychic");
    CostAttack costAttack2 = CostAttackMock.createMockCostAttackSavedInDb(4, attack2, psychicType, 1, false);

    Attack attack3 = AttackMock.createMockAttackSavedInDb(5, "Rest", "0", 0, "Heal all damage");
    CostAttack costAttack3 = CostAttackMock.createMockFreeCostAttackSavedInDb(5, attack3);

    CostAttackEntitiesProcessedDTO dto = new CostAttackEntitiesProcessedDTO();
    dto.setCostAttacks(Arrays.asList(costAttack1, costAttack2, costAttack3));

    Chunk<CostAttackEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    when(costAttackService.getOrCreate(costAttack1)).thenReturn(costAttack1);
    when(costAttackService.getOrCreate(costAttack2)).thenReturn(costAttack2);
    when(costAttackService.getOrCreate(costAttack3)).thenReturn(costAttack3);

    writer.write(chunk);

    verify(costAttackService).getOrCreate(costAttack1);
    verify(costAttackService).getOrCreate(costAttack2);
    verify(costAttackService).getOrCreate(costAttack3);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessMultipleItemsWithMultipleCostAttacks() throws Exception {
    Attack attack1 = AttackMock.createMockAttackSavedInDb(6, "Flamethrower", "90", 3, null);
    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");
    CostAttack costAttack1 = CostAttackMock.createMockCostAttackSavedInDb(6, attack1, fireType, 2, false);

    Attack attack2 = AttackMock.createMockAttackSavedInDb(7, "Thundershock", "30", 1, null);
    Type electricType = TypeMock.createMockTypeSavedInDb(4, "Electric");
    CostAttack costAttack2 = CostAttackMock.createMockCostAttackSavedInDb(7, attack2, electricType, 1, false);

    CostAttackEntitiesProcessedDTO dto1 = new CostAttackEntitiesProcessedDTO();
    dto1.setCostAttacks(List.of(costAttack1));

    CostAttackEntitiesProcessedDTO dto2 = new CostAttackEntitiesProcessedDTO();
    dto2.setCostAttacks(List.of(costAttack2));

    Chunk<CostAttackEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

    when(costAttackService.getOrCreate(costAttack1)).thenReturn(costAttack1);
    when(costAttackService.getOrCreate(costAttack2)).thenReturn(costAttack2);

    writer.write(chunk);

    verify(costAttackService).getOrCreate(costAttack1);
    verify(costAttackService).getOrCreate(costAttack2);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldHandleExceptionInOneOfMultipleCostAttacks() throws Exception {
    Attack attack1 = AttackMock.createMockAttackSavedInDb(11, "First Attack", "30", 1, null);
    Type type1 = TypeMock.createMockTypeSavedInDb(8, "Fighting");
    CostAttack costAttack1 = CostAttackMock.createMockCostAttackSavedInDb(11, attack1, type1, 1, false);

    Attack attack2 = AttackMock.createMockAttackSavedInDb(12, "Failing Attack", "40", 2, null);
    Type type2 = TypeMock.createMockTypeSavedInDb(9, "Bug");
    CostAttack costAttack2 = CostAttackMock.createMockCostAttackSavedInDb(12, attack2, type2, 1, false);

    Attack attack3 = AttackMock.createMockAttackSavedInDb(13, "Third Attack", "50", 1, null);
    Type type3 = TypeMock.createMockTypeSavedInDb(10, "Dragon");
    CostAttack costAttack3 = CostAttackMock.createMockCostAttackSavedInDb(13, attack3, type3, 1, false);

    CostAttackEntitiesProcessedDTO dto = new CostAttackEntitiesProcessedDTO();
    dto.setCostAttacks(Arrays.asList(costAttack1, costAttack2, costAttack3));

    Chunk<CostAttackEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    when(costAttackService.getOrCreate(costAttack1)).thenReturn(costAttack1);
    when(costAttackService.getOrCreate(costAttack2)).thenThrow(new RuntimeException("Error with second cost attack"));

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    writer.write(chunk);

    verify(costAttackService).getOrCreate(costAttack1);
    verify(costAttackService).getOrCreate(costAttack2);
    verify(costAttackService, never()).getOrCreate(costAttack3);
    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());
  }

  @Test
  void write_shouldHandleNullCostAttacksList() throws Exception {
    CostAttackEntitiesProcessedDTO dto = new CostAttackEntitiesProcessedDTO();

    Chunk<CostAttackEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    writer.write(chunk);

    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());
    verifyNoInteractions(costAttackService);
  }
}