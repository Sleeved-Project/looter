package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.common.exception.LooterScrapingException;
import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.infra.dto.AbilityDTO;
import com.sleeved.looter.infra.dto.AttackDTO;
import com.sleeved.looter.infra.dto.BaseCardEntitiesProcessedDTO;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.LegalitiesDTO;
import com.sleeved.looter.infra.mapper.AbilityMapper;
import com.sleeved.looter.infra.mapper.ArtistMapper;
import com.sleeved.looter.infra.mapper.AttackMapper;
import com.sleeved.looter.infra.mapper.LegalitiesMapper;
import com.sleeved.looter.infra.mapper.RarityMapper;
import com.sleeved.looter.infra.mapper.SubtypeMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.AbilityMock;
import com.sleeved.looter.mock.domain.ArtistMock;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.RarityMock;
import com.sleeved.looter.mock.domain.SubtypeMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.infra.CardDTOMock;

@ExtendWith(MockitoExtension.class)
class CardDTOToBaseEntityCardProcessorTest {

  @Mock
  private RarityMapper rarityMapper;

  @Mock
  private ArtistMapper artistMapper;

  @Mock
  private TypeMapper typeMapper;

  @Mock
  private SubtypeMapper subtypeMapper;

  @Mock
  private AbilityMapper abilityMapper;

  @Mock
  private AttackMapper attackMapper;

  @Mock
  private LegalitiesMapper legalitiesMapper;

  @Mock
  private LooterScrapingErrorHandler errorHandler;

  @InjectMocks
  private CardDTOToBaseEntityCardProcessor processor;

  @Test
  void process_shouldConvertCardDTOToBaseCardEntitiesProcessedDTO() throws Exception {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("test-card", "Test Card");
    cardDTO.setRarity("Rare");
    cardDTO.setArtist("Test Artist");
    cardDTO.setTypes(Arrays.asList("Pokemon"));
    cardDTO.setSubtypes(Arrays.asList("Basic"));

    Rarity mockRarity = RarityMock.createMockRarity("Rare");
    Artist mockArtist = ArtistMock.createMockArtist("Test Artist");
    List<Type> mockTypes = Arrays.asList(TypeMock.createMockType("Pokemon"));
    List<Subtype> mockSubtypes = Arrays.asList(SubtypeMock.createMockSubtype("Basic"));
    List<Ability> mockAbilities = Arrays.asList();
    List<Attack> mockAttacks = Arrays.asList();
    Legalities mockLegalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    when(rarityMapper.toEntity(any())).thenReturn(mockRarity);
    when(artistMapper.toEntity(any())).thenReturn(mockArtist);
    when(typeMapper.toListEntity(any())).thenReturn(mockTypes);
    when(subtypeMapper.toListEntity(any())).thenReturn(mockSubtypes);
    when(abilityMapper.toListEntity(any())).thenReturn(mockAbilities);
    when(attackMapper.toListEntity(any())).thenReturn(mockAttacks);
    when(legalitiesMapper.toEntity(any())).thenReturn(mockLegalities);

    BaseCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getRarity()).isEqualTo(mockRarity);
    assertThat(result.getArtist()).isEqualTo(mockArtist);
    assertThat(result.getTypes()).isEqualTo(mockTypes);
    assertThat(result.getSubtypes()).isEqualTo(mockSubtypes);
    assertThat(result.getAbilities()).isEqualTo(mockAbilities);
    assertThat(result.getAttacks()).isEqualTo(mockAttacks);
    assertThat(result.getLegalities()).isEqualTo(mockLegalities);
  }

  @Test
  void process_shouldHandleNullValues() throws Exception {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("test-card", "Test Card");

    when(rarityMapper.toEntity(null)).thenReturn(null);
    when(artistMapper.toEntity(null)).thenReturn(null);
    when(typeMapper.toListEntity(null)).thenReturn(List.of());
    when(subtypeMapper.toListEntity(null)).thenReturn(List.of());
    when(abilityMapper.toListEntity(null)).thenReturn(List.of());
    when(attackMapper.toListEntity(null)).thenReturn(List.of());
    when(legalitiesMapper.toEntity(null)).thenReturn(null);

    BaseCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getRarity()).isNull();
    assertThat(result.getArtist()).isNull();
    assertThat(result.getTypes()).isEmpty();
    assertThat(result.getSubtypes()).isEmpty();
    assertThat(result.getAbilities()).isEmpty();
    assertThat(result.getAttacks()).isEmpty();
    assertThat(result.getLegalities()).isNull();
  }

  @Test
  void process_shouldHandleCompleteCardDTO() throws Exception {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("test-card", "Test Card");
    cardDTO.setRarity("Ultra Rare");
    cardDTO.setArtist("Famous Artist");
    cardDTO.setTypes(Arrays.asList("Pokemon", "Fire"));
    cardDTO.setSubtypes(Arrays.asList("Basic", "EX"));

    AbilityDTO ability = new AbilityDTO();
    ability.setName("Special Ability");
    ability.setText("Special ability text");
    ability.setType("Ability");
    cardDTO.setAbilities(Arrays.asList(ability));

    AttackDTO attack = new AttackDTO();
    attack.setName("Fire Blast");
    attack.setDamage("120");
    attack.setConvertedEnergyCost(3);
    attack.setText("Discard 2 energy cards");
    cardDTO.setAttacks(Arrays.asList(attack));

    LegalitiesDTO legalities = new LegalitiesDTO();
    legalities.setStandard("Legal");
    legalities.setExpanded("Legal");
    legalities.setUnlimited("Legal");
    cardDTO.setLegalities(legalities);

    Rarity mockRarity = RarityMock.createMockRarity("Ultra Rare");
    Artist mockArtist = ArtistMock.createMockArtist("Famous Artist");
    List<Type> mockTypes = Arrays.asList(
        TypeMock.createMockType("Pokemon"),
        TypeMock.createMockType("Fire"));
    List<Subtype> mockSubtypes = Arrays.asList(
        SubtypeMock.createMockSubtype("Basic"),
        SubtypeMock.createMockSubtype("EX"));
    List<Ability> mockAbilities = Arrays.asList(
        AbilityMock.createMockAbility("Special Ability", "Special ability text", "Ability"));
    List<Attack> mockAttacks = Arrays.asList(
        AttackMock.createMockAttack("Fire Blast", "120", 3, "Discard 2 energy cards"));
    Legalities mockLegalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    when(rarityMapper.toEntity(any())).thenReturn(mockRarity);
    when(artistMapper.toEntity(any())).thenReturn(mockArtist);
    when(typeMapper.toListEntity(any())).thenReturn(mockTypes);
    when(subtypeMapper.toListEntity(any())).thenReturn(mockSubtypes);
    when(abilityMapper.toListEntity(any())).thenReturn(mockAbilities);
    when(attackMapper.toListEntity(any())).thenReturn(mockAttacks);
    when(legalitiesMapper.toEntity(any())).thenReturn(mockLegalities);

    BaseCardEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getRarity()).isEqualTo(mockRarity);
    assertThat(result.getArtist()).isEqualTo(mockArtist);
    assertThat(result.getTypes()).isEqualTo(mockTypes);
    assertThat(result.getSubtypes()).isEqualTo(mockSubtypes);
    assertThat(result.getAbilities()).isEqualTo(mockAbilities);
    assertThat(result.getAttacks()).isEqualTo(mockAttacks);
    assertThat(result.getLegalities()).isEqualTo(mockLegalities);
  }

  @Test
  void process_shouldHandleExceptionWithErrorHandler() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("test-card", "Test Card");
    Exception mappingException = new RuntimeException("Mapping error");

    when(rarityMapper.toEntity(any())).thenThrow(mappingException);

    LooterScrapingException expectedException = new LooterScrapingException("Error processing card", mappingException);
    doThrow(expectedException).when(errorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());

    assertThatThrownBy(() -> processor.process(cardDTO))
        .isInstanceOf(LooterScrapingException.class)
        .hasMessage("Error processing card");
  }
}
