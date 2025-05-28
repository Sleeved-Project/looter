package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.LegalitiesService;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.LegalitiesDTO;
import com.sleeved.looter.infra.dto.ResistanceDTO;
import com.sleeved.looter.infra.dto.SetDTO;
import com.sleeved.looter.infra.dto.SetsWeaknessResistanceCardEntitiesProcessedDTO;
import com.sleeved.looter.infra.dto.WeaknessDTO;
import com.sleeved.looter.infra.mapper.LegalitiesMapper;
import com.sleeved.looter.infra.mapper.ResistanceMapper;
import com.sleeved.looter.infra.mapper.SetMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.mapper.WeaknessMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.ResistanceMock;
import com.sleeved.looter.mock.domain.SetMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;
import com.sleeved.looter.mock.infra.CardDTOMock;
import com.sleeved.looter.mock.infra.LegalitiesDTOMock;
import com.sleeved.looter.mock.infra.ResistanceDTOMock;
import com.sleeved.looter.mock.infra.SetDTOMock;
import com.sleeved.looter.mock.infra.WeaknessDTOMock;

@ExtendWith(MockitoExtension.class)
class CardDTOToSetsWeaknessResistanceCardProcessorTest {

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @Mock
  private SetMapper setMapper;

  @Mock
  private LegalitiesMapper legalitiesMapper;

  @Mock
  private LegalitiesService legalitiesService;

  @Mock
  private TypeMapper typeMapper;

  @Mock
  private WeaknessMapper weaknessMapper;

  @Mock
  private ResistanceMapper resistanceMapper;

  @Mock
  private TypeService typeService;

  @InjectMocks
  private CardDTOToSetsWeaknessResistanceCardProcessor processor;

  @Captor
  private ArgumentCaptor<String> errorMessageCaptor;

  @Test
  void process_shouldReturnCorrectDTO_whenValidInput() {
    // Input mock data
    LegalitiesDTO legalitiesDTO = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal");
    SetDTO setDTO = SetDTOMock.createMockSetDTO("base1", "Base Set", "Base", 102, 102, "BS",
        "2023/05/15", "2023/05/15 10:30:45",
        "symbol-base.png", "logo-base.png");
    setDTO.setLegalities(legalitiesDTO);

    WeaknessDTO weaknessDTO = WeaknessDTOMock.createMockWeaknessDTO("Fire", "×2");
    ResistanceDTO resistanceDTO = ResistanceDTOMock.createMockResistanceDTO("Water", "-30");

    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setSet(setDTO);
    cardDTO.setWeaknesses(Arrays.asList(weaknessDTO));
    cardDTO.setResistances(Arrays.asList(resistanceDTO));

    // Output mock data
    Legalities setLegalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", setLegalities);

    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Type waterType = TypeMock.createMockTypeSavedInDb(2, "Water");

    Weakness weakness = WeaknessMock.createMockWeaknessSavedInDb(1, fireType, "×2");
    Resistance resistance = ResistanceMock.createMockResistanceSavedInDb(1, waterType, "-30");

    // Spy on the mappers and services
    when(legalitiesMapper.toEntity(legalitiesDTO)).thenReturn(setLegalities);
    when(legalitiesService.getByStandardExpandedUnlimited(setLegalities)).thenReturn(setLegalities);
    when(setMapper.toEntity(setDTO, setLegalities)).thenReturn(set);

    when(typeMapper.toEntity("Fire")).thenReturn(fireType);
    when(typeService.getByLabel(fireType)).thenReturn(fireType);
    when(weaknessMapper.toEntity(weaknessDTO, fireType)).thenReturn(weakness);

    when(typeMapper.toEntity("Water")).thenReturn(waterType);
    when(typeService.getByLabel(waterType)).thenReturn(waterType);
    when(resistanceMapper.toEntity(resistanceDTO, waterType)).thenReturn(resistance);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getWeaknesses()).hasSize(1);
    assertThat(result.getWeaknesses().get(0)).isEqualTo(weakness);
    assertThat(result.getResistances()).hasSize(1);
    assertThat(result.getResistances().get(0)).isEqualTo(resistance);
  }

  @Test
  void process_shouldReturnNullAndLogError_whenExceptionOccurs() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setSet(null); // Pour provoquer une NullPointerException

    doNothing().when(looterScrapingErrorHandler).handle(
        any(Exception.class), anyString(), anyString(), anyString());
    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNull();
    verify(looterScrapingErrorHandler).handle(
        any(Exception.class), anyString(), anyString(), anyString());
  }

  @Test
  void processWeaknesses_shouldReturnEmptyList_whenWeaknessesIsNull() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setWeaknesses(null);

    LegalitiesDTO legalitiesDTO = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal");
    SetDTO setDTO = SetDTOMock.createMockSetDTO("base1", "Base Set", "Base", 102, 102, "BS",
        "2023/05/15", "2023/05/15 10:30:45",
        "symbol-base.png", "logo-base.png");
    setDTO.setLegalities(legalitiesDTO);
    cardDTO.setSet(setDTO);

    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", legalities);

    when(legalitiesMapper.toEntity(legalitiesDTO)).thenReturn(legalities);
    when(legalitiesService.getByStandardExpandedUnlimited(legalities)).thenReturn(legalities);
    when(setMapper.toEntity(setDTO, legalities)).thenReturn(set);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getWeaknesses()).isEmpty();
  }

  @Test
  void processWeaknesses_shouldProcessAllWeaknesses_whenWeaknessesIsNotEmpty() {
    WeaknessDTO weakness1DTO = WeaknessDTOMock.createMockWeaknessDTO("Fire", "×2");
    WeaknessDTO weakness2DTO = WeaknessDTOMock.createMockWeaknessDTO("Lightning", "×2");

    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setWeaknesses(Arrays.asList(weakness1DTO, weakness2DTO));

    LegalitiesDTO legalitiesDTO = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal");
    SetDTO setDTO = SetDTOMock.createMockSetDTO("base1", "Base Set", "Base", 102, 102, "BS",
        "2023/05/15", "2023/05/15 10:30:45",
        "symbol-base.png", "logo-base.png");
    setDTO.setLegalities(legalitiesDTO);
    cardDTO.setSet(setDTO);

    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", legalities);

    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Type lightningType = TypeMock.createMockTypeSavedInDb(3, "Lightning");

    Weakness weakness1 = WeaknessMock.createMockWeaknessSavedInDb(1, fireType, "×2");
    Weakness weakness2 = WeaknessMock.createMockWeaknessSavedInDb(2, lightningType, "×2");

    when(legalitiesMapper.toEntity(legalitiesDTO)).thenReturn(legalities);
    when(legalitiesService.getByStandardExpandedUnlimited(legalities)).thenReturn(legalities);
    when(setMapper.toEntity(setDTO, legalities)).thenReturn(set);

    when(typeMapper.toEntity("Fire")).thenReturn(fireType);
    when(typeService.getByLabel(fireType)).thenReturn(fireType);
    when(weaknessMapper.toEntity(weakness1DTO, fireType)).thenReturn(weakness1);

    when(typeMapper.toEntity("Lightning")).thenReturn(lightningType);
    when(typeService.getByLabel(lightningType)).thenReturn(lightningType);
    when(weaknessMapper.toEntity(weakness2DTO, lightningType)).thenReturn(weakness2);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getWeaknesses()).hasSize(2);
    assertThat(result.getWeaknesses()).contains(weakness1, weakness2);

    verify(typeMapper, times(2)).toEntity(anyString());
    verify(typeService, times(2)).getByLabel(any(Type.class));
    verify(weaknessMapper, times(2)).toEntity(any(WeaknessDTO.class), any(Type.class));
  }

  @Test
  void processResistances_shouldReturnEmptyList_whenResistancesIsNull() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setResistances(null);

    LegalitiesDTO legalitiesDTO = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal");
    SetDTO setDTO = SetDTOMock.createMockSetDTO("base1", "Base Set", "Base", 102, 102, "BS",
        "2023/05/15", "2023/05/15 10:30:45",
        "symbol-base.png", "logo-base.png");
    setDTO.setLegalities(legalitiesDTO);
    cardDTO.setSet(setDTO);

    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", legalities);

    when(legalitiesMapper.toEntity(legalitiesDTO)).thenReturn(legalities);
    when(legalitiesService.getByStandardExpandedUnlimited(legalities)).thenReturn(legalities);
    when(setMapper.toEntity(setDTO, legalities)).thenReturn(set);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getResistances()).isEmpty();
  }

  @Test
  void processResistances_shouldProcessAllResistances_whenResistancesIsNotEmpty() {
    ResistanceDTO resistance1DTO = ResistanceDTOMock.createMockResistanceDTO("Water", "-30");
    ResistanceDTO resistance2DTO = ResistanceDTOMock.createMockResistanceDTO("Fighting", "-20");

    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setResistances(Arrays.asList(resistance1DTO, resistance2DTO));

    LegalitiesDTO legalitiesDTO = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal");
    SetDTO setDTO = SetDTOMock.createMockSetDTO("base1", "Base Set", "Base", 102, 102, "BS",
        "2023/05/15", "2023/05/15 10:30:45",
        "symbol-base.png", "logo-base.png");
    setDTO.setLegalities(legalitiesDTO);
    cardDTO.setSet(setDTO);

    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", legalities);

    Type waterType = TypeMock.createMockTypeSavedInDb(2, "Water");
    Type fightingType = TypeMock.createMockTypeSavedInDb(4, "Fighting");

    Resistance resistance1 = ResistanceMock.createMockResistanceSavedInDb(1, waterType, "-30");
    Resistance resistance2 = ResistanceMock.createMockResistanceSavedInDb(2, fightingType, "-20");

    when(legalitiesMapper.toEntity(legalitiesDTO)).thenReturn(legalities);
    when(legalitiesService.getByStandardExpandedUnlimited(legalities)).thenReturn(legalities);
    when(setMapper.toEntity(setDTO, legalities)).thenReturn(set);

    when(typeMapper.toEntity("Water")).thenReturn(waterType);
    when(typeService.getByLabel(waterType)).thenReturn(waterType);
    when(resistanceMapper.toEntity(resistance1DTO, waterType)).thenReturn(resistance1);

    when(typeMapper.toEntity("Fighting")).thenReturn(fightingType);
    when(typeService.getByLabel(fightingType)).thenReturn(fightingType);
    when(resistanceMapper.toEntity(resistance2DTO, fightingType)).thenReturn(resistance2);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getResistances()).hasSize(2);
    assertThat(result.getResistances()).contains(resistance1, resistance2);

    verify(typeMapper, times(2)).toEntity(anyString());
    verify(typeService, times(2)).getByLabel(any(Type.class));
    verify(resistanceMapper, times(2)).toEntity(any(ResistanceDTO.class), any(Type.class));
  }

  @Test
  void process_shouldHandleNullWeaknessesAndResistances() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setWeaknesses(null);
    cardDTO.setResistances(null);

    LegalitiesDTO legalitiesDTO = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal");
    SetDTO setDTO = SetDTOMock.createMockSetDTO("base1", "Base Set", "Base", 102, 102, "BS",
        "2023/05/15", "2023/05/15 10:30:45",
        "symbol-base.png", "logo-base.png");
    setDTO.setLegalities(legalitiesDTO);
    cardDTO.setSet(setDTO);

    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", legalities);

    when(legalitiesMapper.toEntity(legalitiesDTO)).thenReturn(legalities);
    when(legalitiesService.getByStandardExpandedUnlimited(legalities)).thenReturn(legalities);
    when(setMapper.toEntity(setDTO, legalities)).thenReturn(set);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getWeaknesses()).isEmpty();
    assertThat(result.getResistances()).isEmpty();
  }

  @Test
  void process_shouldHandleEmptyWeaknessesAndResistances() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setWeaknesses(Collections.emptyList());
    cardDTO.setResistances(Collections.emptyList());

    LegalitiesDTO legalitiesDTO = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal");
    SetDTO setDTO = SetDTOMock.createMockSetDTO("base1", "Base Set", "Base", 102, 102, "BS",
        "2023/05/15", "2023/05/15 10:30:45",
        "symbol-base.png", "logo-base.png");
    setDTO.setLegalities(legalitiesDTO);
    cardDTO.setSet(setDTO);

    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", legalities);

    when(legalitiesMapper.toEntity(legalitiesDTO)).thenReturn(legalities);
    when(legalitiesService.getByStandardExpandedUnlimited(legalities)).thenReturn(legalities);
    when(setMapper.toEntity(setDTO, legalities)).thenReturn(set);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getWeaknesses()).isEmpty();
    assertThat(result.getResistances()).isEmpty();
  }
}