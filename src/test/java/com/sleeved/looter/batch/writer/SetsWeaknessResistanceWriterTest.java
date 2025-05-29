package com.sleeved.looter.batch.writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.ResistanceService;
import com.sleeved.looter.domain.service.SetService;
import com.sleeved.looter.domain.service.WeaknessService;
import com.sleeved.looter.infra.dto.SetsWeaknessResistanceCardEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.ResistanceMock;
import com.sleeved.looter.mock.domain.SetMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;

@ExtendWith(MockitoExtension.class)
class SetsWeaknessResistanceWriterTest {

  @Mock
  private SetService setService;

  @Mock
  private WeaknessService weaknessService;

  @Mock
  private ResistanceService resistanceService;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private SetsWeaknessResistanceWriter writer;

  @Test
  void write_shouldProcessEmptyChunk() throws Exception {
    Chunk<SetsWeaknessResistanceCardEntitiesProcessedDTO> chunk = new Chunk<>(Collections.emptyList());

    writer.write(chunk);

    verifyNoInteractions(setService, weaknessService, resistanceService, looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessSingleItemChunk() throws Exception {
    SetsWeaknessResistanceCardEntitiesProcessedDTO dto = new SetsWeaknessResistanceCardEntitiesProcessedDTO();

    Legalities legalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("swsh09", "Brilliant Stars", "Sword & Shield", 186, 200, "BST",
        "url/to/symbol.png", "url/to/logo.png", legalities);

    Type waterType = TypeMock.createMockType("Water");
    Type fightingType = TypeMock.createMockType("Fighting");

    List<Weakness> weaknesses = Arrays.asList(WeaknessMock.createMockWeakness(waterType, "×2"));
    List<Resistance> resistances = Arrays.asList(ResistanceMock.createMockResistance(fightingType, "-30"));

    dto.setSet(set);
    dto.setWeaknesses(weaknesses);
    dto.setResistances(resistances);

    Chunk<SetsWeaknessResistanceCardEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto));

    writer.write(chunk);

    verify(setService).getOrCreate(set);
    verify(weaknessService).getOrCreate(weaknesses.get(0));
    verify(resistanceService).getOrCreate(resistances.get(0));
  }

  @Test
  void write_shouldProcessMultipleItemChunk() throws Exception {
    SetsWeaknessResistanceCardEntitiesProcessedDTO dto1 = new SetsWeaknessResistanceCardEntitiesProcessedDTO();
    SetsWeaknessResistanceCardEntitiesProcessedDTO dto2 = new SetsWeaknessResistanceCardEntitiesProcessedDTO();

    Legalities legalities1 = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Set set1 = SetMock.createMockSet("swsh09", "Brilliant Stars", "Sword & Shield", 186, 200, "BST",
        "url/to/symbol1.png", "url/to/logo1.png", legalities1);

    Type waterType = TypeMock.createMockType("Water");
    Type fightingType = TypeMock.createMockType("Fighting");

    List<Weakness> weaknesses1 = Arrays.asList(WeaknessMock.createMockWeakness(waterType, "×2"));
    List<Resistance> resistances1 = Arrays.asList(ResistanceMock.createMockResistance(fightingType, "-30"));

    dto1.setSet(set1);
    dto1.setWeaknesses(weaknesses1);
    dto1.setResistances(resistances1);

    Legalities legalities2 = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Set set2 = SetMock.createMockSet("swsh12", "Silver Tempest", "Sword & Shield", 195, 220, "SIT",
        "url/to/symbol2.png", "url/to/logo2.png", legalities2);

    Type fireType = TypeMock.createMockType("Fire");
    Type lightningType = TypeMock.createMockType("Lightning");
    Type grassType = TypeMock.createMockType("Grass");

    List<Weakness> weaknesses2 = Arrays.asList(
        WeaknessMock.createMockWeakness(fireType, "×2"),
        WeaknessMock.createMockWeakness(lightningType, "×2"));
    List<Resistance> resistances2 = Arrays.asList(ResistanceMock.createMockResistance(grassType, "-30"));

    dto2.setSet(set2);
    dto2.setWeaknesses(weaknesses2);
    dto2.setResistances(resistances2);

    Chunk<SetsWeaknessResistanceCardEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

    writer.write(chunk);

    verify(setService).getOrCreate(set1);
    verify(setService).getOrCreate(set2);
    verify(weaknessService).getOrCreate(weaknesses1.get(0));
    verify(weaknessService).getOrCreate(weaknesses2.get(0));
    verify(weaknessService).getOrCreate(weaknesses2.get(1));
    verify(resistanceService).getOrCreate(resistances1.get(0));
    verify(resistanceService).getOrCreate(resistances2.get(0));
  }

  @Test
  void write_shouldHandleExceptionWithErrorHandler() throws Exception {
    SetsWeaknessResistanceCardEntitiesProcessedDTO dto = new SetsWeaknessResistanceCardEntitiesProcessedDTO();

    Legalities legalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("swsh09", "Brilliant Stars", "Sword & Shield", 186, 200, "BST",
        "url/to/symbol.png", "url/to/logo.png", legalities);

    Type waterType = TypeMock.createMockType("Water");
    Type fightingType = TypeMock.createMockType("Fighting");

    List<Weakness> weaknesses = Arrays.asList(WeaknessMock.createMockWeakness(waterType, "×2"));
    List<Resistance> resistances = Arrays.asList(ResistanceMock.createMockResistance(fightingType, "-30"));

    dto.setSet(set);
    dto.setWeaknesses(weaknesses);
    dto.setResistances(resistances);

    Exception serviceException = new RuntimeException("Service error");
    when(setService.getOrCreate(any())).thenThrow(serviceException);

    String formattedItemValue = "SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES: SetsWeaknessResistanceCardEntitiesProcessedDTO(...)";
    when(looterScrapingErrorHandler.formatErrorItem(
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_ITEM),
        anyString())).thenReturn(formattedItemValue);

    writer.write(new Chunk<>(List.of(dto)));

    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_ITEM),
        anyString());

    verify(looterScrapingErrorHandler).handle(
        eq(serviceException),
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        eq(formattedItemValue));
  }

  @Test
  void write_shouldContinueProcessingChunkAfterExceptionInOneItem() throws Exception {
    SetsWeaknessResistanceCardEntitiesProcessedDTO dto1 = new SetsWeaknessResistanceCardEntitiesProcessedDTO();
    SetsWeaknessResistanceCardEntitiesProcessedDTO dto2 = new SetsWeaknessResistanceCardEntitiesProcessedDTO();

    Legalities legalities1 = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Legalities legalities2 = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    Set set1 = SetMock.createMockSet("swsh09", "Brilliant Stars", "Sword & Shield", 186, 200, "BST",
        "url/to/symbol1.png", "url/to/logo1.png", legalities1);
    Set set2 = SetMock.createMockSet("swsh12", "Silver Tempest", "Sword & Shield", 195, 220, "SIT",
        "url/to/symbol2.png", "url/to/logo2.png", legalities2);

    dto1.setSet(set1);
    dto1.setWeaknesses(Collections.emptyList());
    dto1.setResistances(Collections.emptyList());

    dto2.setSet(set2);
    dto2.setWeaknesses(Collections.emptyList());
    dto2.setResistances(Collections.emptyList());

    when(setService.getOrCreate(set1)).thenThrow(new RuntimeException("Error with first set"));
    when(setService.getOrCreate(set2)).thenReturn(set2);

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    Chunk<SetsWeaknessResistanceCardEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

    writer.write(chunk);

    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());

    verify(setService).getOrCreate(set2);
  }
}