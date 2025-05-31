package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sleeved.looter.common.exception.LooterScrapingException;
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
import com.sleeved.looter.infra.mapper.SetMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.processor.ResistanceProcessor;
import com.sleeved.looter.infra.processor.WeaknessProcessor;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.SetMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;
import com.sleeved.looter.mock.domain.ResistanceMock;
import com.sleeved.looter.mock.infra.CardDTOMock;

@ExtendWith(MockitoExtension.class)
class CardDTOToSetsWeaknessResistanceCardProcessorTest {

  @Mock
  private LegalitiesMapper legalitiesMapper;

  @Mock
  private LegalitiesService legalitiesService;

  @Mock
  private SetMapper setMapper;

  @Mock
  private TypeMapper typeMapper;

  @Mock
  private TypeService typeService;

  @Mock
  private WeaknessProcessor weaknessProcessor;

  @Mock
  private ResistanceProcessor resistanceProcessor;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private CardDTOToSetsWeaknessResistanceCardProcessor processor;

  @Test
  void process_shouldConvertCardDTOToSetsWeaknessResistanceCardEntitiesProcessedDTO() throws Exception {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("test-card", "Test Card");

    SetDTO setDTO = new SetDTO();
    LegalitiesDTO legalitiesDTO = new LegalitiesDTO();
    legalitiesDTO.setStandard("Legal");
    legalitiesDTO.setExpanded("Legal");
    legalitiesDTO.setUnlimited("Legal");
    setDTO.setLegalities(legalitiesDTO);
    setDTO.setId("test-set");
    setDTO.setName("Test Set");
    cardDTO.setSet(setDTO);

    List<WeaknessDTO> weaknessDTOs = new ArrayList<>();
    List<ResistanceDTO> resistanceDTOs = new ArrayList<>();
    cardDTO.setWeaknesses(weaknessDTOs);
    cardDTO.setResistances(resistanceDTOs);

    Legalities setLegalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("test-set", "Test Set", "Test Series", 100, 100, "TST", "url-to-symbol",
        "url-to-logo", setLegalities);
    List<Weakness> weaknesses = new ArrayList<>();
    List<Resistance> resistances = new ArrayList<>();

    when(legalitiesMapper.toEntity(setDTO.getLegalities())).thenReturn(setLegalities);
    when(legalitiesService.getByStandardExpandedUnlimited(setLegalities)).thenReturn(setLegalities);
    when(setMapper.toEntity(setDTO, setLegalities)).thenReturn(set);
    when(weaknessProcessor.processFromDTOs(weaknessDTOs)).thenReturn(weaknesses);
    when(resistanceProcessor.processFromDTOs(resistanceDTOs)).thenReturn(resistances);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getWeaknesses()).isEqualTo(weaknesses);
    assertThat(result.getResistances()).isEqualTo(resistances);
  }

  @Test
  void process_shouldHandleNullWeaknessesAndResistances() throws Exception {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("test-card", "Test Card");

    SetDTO setDTO = new SetDTO();
    LegalitiesDTO legalitiesDTO = new LegalitiesDTO();
    legalitiesDTO.setStandard("Legal");
    legalitiesDTO.setExpanded("Legal");
    legalitiesDTO.setUnlimited("Legal");
    setDTO.setLegalities(legalitiesDTO);
    setDTO.setId("test-set");
    setDTO.setName("Test Set");
    cardDTO.setSet(setDTO);

    cardDTO.setWeaknesses(null);
    cardDTO.setResistances(null);

    Legalities setLegalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("test-set", "Test Set", "Test Series", 100, 100, "TST", "url-to-symbol",
        "url-to-logo", setLegalities);
    List<Weakness> weaknesses = Collections.emptyList();
    List<Resistance> resistances = Collections.emptyList();

    when(legalitiesMapper.toEntity(setDTO.getLegalities())).thenReturn(setLegalities);
    when(legalitiesService.getByStandardExpandedUnlimited(setLegalities)).thenReturn(setLegalities);
    when(setMapper.toEntity(setDTO, setLegalities)).thenReturn(set);
    when(weaknessProcessor.processFromDTOs(null)).thenReturn(weaknesses);
    when(resistanceProcessor.processFromDTOs(null)).thenReturn(resistances);

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getWeaknesses()).isEqualTo(weaknesses);
    assertThat(result.getResistances()).isEqualTo(resistances);
  }

  @Test
  void process_shouldHandleWeaknessesAndResistances() throws Exception {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("test-card", "Test Card");

    SetDTO setDTO = new SetDTO();
    LegalitiesDTO legalitiesDTO = new LegalitiesDTO();
    legalitiesDTO.setStandard("Legal");
    legalitiesDTO.setExpanded("Legal");
    legalitiesDTO.setUnlimited("Legal");
    setDTO.setLegalities(legalitiesDTO);
    setDTO.setId("test-set");
    setDTO.setName("Test Set");
    cardDTO.setSet(setDTO);

    WeaknessDTO weaknessDTO = new WeaknessDTO();
    weaknessDTO.setType("Fire");
    weaknessDTO.setValue("×2");

    ResistanceDTO resistanceDTO = new ResistanceDTO();
    resistanceDTO.setType("Water");
    resistanceDTO.setValue("-20");

    cardDTO.setWeaknesses(Arrays.asList(weaknessDTO));
    cardDTO.setResistances(Arrays.asList(resistanceDTO));

    Legalities setLegalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("test-set", "Test Set", "Test Series", 100, 100, "TST", "url-to-symbol",
        "url-to-logo", setLegalities);

    Type fireType = TypeMock.createMockType("Fire");
    Type waterType = TypeMock.createMockType("Water");

    Weakness weakness = WeaknessMock.createMockWeakness(fireType, "×2");
    Resistance resistance = ResistanceMock.createMockResistance(waterType, "-20");

    when(legalitiesMapper.toEntity(setDTO.getLegalities())).thenReturn(setLegalities);
    when(legalitiesService.getByStandardExpandedUnlimited(setLegalities)).thenReturn(setLegalities);
    when(setMapper.toEntity(setDTO, setLegalities)).thenReturn(set);
    when(weaknessProcessor.processFromDTOs(cardDTO.getWeaknesses())).thenReturn(Arrays.asList(weakness));
    when(resistanceProcessor.processFromDTOs(cardDTO.getResistances())).thenReturn(Arrays.asList(resistance));

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getWeaknesses()).containsExactly(weakness);
    assertThat(result.getResistances()).containsExactly(resistance);
  }

  @Test
  void process_shouldHandleExceptionWithErrorHandler() throws Exception {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("test-card", "Test Card");

    SetDTO setDTO = new SetDTO();
    LegalitiesDTO legalitiesDTO = new LegalitiesDTO();
    setDTO.setLegalities(legalitiesDTO);
    cardDTO.setSet(setDTO);

    LooterScrapingException exception = new LooterScrapingException("Test exception");
    when(legalitiesMapper.toEntity(any())).thenThrow(exception);
    when(looterScrapingErrorHandler.formatErrorItem(any(), any())).thenReturn("Formatted error");

    SetsWeaknessResistanceCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNull();
    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        any(String.class),
        any(String.class),
        any(String.class));
  }
}