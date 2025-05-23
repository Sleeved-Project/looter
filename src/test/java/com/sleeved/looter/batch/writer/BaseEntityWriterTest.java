package com.sleeved.looter.batch.writer;

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

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.AbilityService;
import com.sleeved.looter.domain.service.ArtistService;
import com.sleeved.looter.domain.service.AttackService;
import com.sleeved.looter.domain.service.LegalitiesService;
import com.sleeved.looter.domain.service.RarityService;
import com.sleeved.looter.domain.service.SubtypeService;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.BaseCardEntitiesProcessedDTO;
import com.sleeved.looter.mock.domain.AbilityMock;
import com.sleeved.looter.mock.domain.ArtistMock;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.RarityMock;
import com.sleeved.looter.mock.domain.SubtypeMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
class BaseEntityWriterTest {

  @Mock
  private RarityService rarityService;

  @Mock
  private ArtistService artistService;

  @Mock
  private TypeService typeService;

  @Mock
  private SubtypeService subtypeService;

  @Mock
  private AbilityService abilityService;

  @Mock
  private AttackService attackService;

  @Mock
  private LegalitiesService legalityService;

  @InjectMocks
  private BaseEntityWriter writer;

  @Test
  void write_shouldProcessEmptyChunk() throws Exception {
    Chunk<BaseCardEntitiesProcessedDTO> chunk = new Chunk<>(Collections.emptyList());

    writer.write(chunk);

    verifyNoInteractions(rarityService, artistService, typeService, subtypeService,
        abilityService, attackService, legalityService);
  }

  @Test
  void write_shouldProcessSingleItemChunk() throws Exception {
    BaseCardEntitiesProcessedDTO dto = new BaseCardEntitiesProcessedDTO();

    Rarity rarity = RarityMock.createMockRarity("Rare");
    Artist artist = ArtistMock.createMockArtist("Test Artist");
    List<Type> types = Arrays.asList(TypeMock.createMockType("Pokemon"));
    List<Subtype> subtypes = Arrays.asList(SubtypeMock.createMockSubtype("Basic"));
    List<Ability> abilities = Arrays.asList(AbilityMock.createMockAbility("Test Ability", "Test text", "Ability"));
    List<Attack> attacks = Arrays.asList(AttackMock.createMockAttack("Test Attack", "100", 2, "Test description"));
    Legalities legalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    dto.setRarity(rarity);
    dto.setArtist(artist);
    dto.setTypes(types);
    dto.setSubtypes(subtypes);
    dto.setAbilities(abilities);
    dto.setAttacks(attacks);
    dto.setLegalities(legalities);

    Chunk<BaseCardEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto));

    writer.write(chunk);

    verify(rarityService).getOrCreate(rarity);
    verify(artistService).getOrCreate(artist);
    verify(typeService).getOrCreate(types.get(0));
    verify(subtypeService).getOrCreate(subtypes.get(0));
    verify(abilityService).getOrCreate(abilities.get(0));
    verify(attackService).getOrCreate(attacks.get(0));
    verify(legalityService).getOrCreate(legalities);
  }

  @Test
  void write_shouldProcessMultipleItemChunk() throws Exception {
    BaseCardEntitiesProcessedDTO dto1 = new BaseCardEntitiesProcessedDTO();
    BaseCardEntitiesProcessedDTO dto2 = new BaseCardEntitiesProcessedDTO();

    Rarity rarity1 = RarityMock.createMockRarity("Rare");
    Artist artist1 = ArtistMock.createMockArtist("Artist 1");
    List<Type> types1 = Arrays.asList(TypeMock.createMockType("Pokemon"));
    List<Subtype> subtypes1 = Arrays.asList(SubtypeMock.createMockSubtype("Basic"));
    List<Ability> abilities1 = Arrays.asList(AbilityMock.createMockAbility("Ability 1", "Text 1", "Ability"));
    List<Attack> attacks1 = Arrays.asList(AttackMock.createMockAttack("Attack 1", "100", 2, "Description 1"));
    Legalities legalities1 = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    dto1.setRarity(rarity1);
    dto1.setArtist(artist1);
    dto1.setTypes(types1);
    dto1.setSubtypes(subtypes1);
    dto1.setAbilities(abilities1);
    dto1.setAttacks(attacks1);
    dto1.setLegalities(legalities1);

    Rarity rarity2 = RarityMock.createMockRarity("Uncommon");
    Artist artist2 = ArtistMock.createMockArtist("Artist 2");
    List<Type> types2 = Arrays.asList(TypeMock.createMockType("Trainer"));
    List<Subtype> subtypes2 = Arrays.asList(SubtypeMock.createMockSubtype("Item"));
    List<Ability> abilities2 = Collections.emptyList(); // Pas d'abilities pour ce DTO
    List<Attack> attacks2 = Collections.emptyList(); // Pas d'attacks pour ce DTO
    Legalities legalities2 = LegalitiesMock.createMockLegalities("Not Legal", "Legal", "Legal");

    dto2.setRarity(rarity2);
    dto2.setArtist(artist2);
    dto2.setTypes(types2);
    dto2.setSubtypes(subtypes2);
    dto2.setAbilities(abilities2);
    dto2.setAttacks(attacks2);
    dto2.setLegalities(legalities2);

    Chunk<BaseCardEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

    writer.write(chunk);

    verify(rarityService).getOrCreate(rarity1);
    verify(rarityService).getOrCreate(rarity2);
    verify(artistService).getOrCreate(artist1);
    verify(artistService).getOrCreate(artist2);
    verify(typeService).getOrCreate(types1.get(0));
    verify(typeService).getOrCreate(types2.get(0));
    verify(subtypeService).getOrCreate(subtypes1.get(0));
    verify(subtypeService).getOrCreate(subtypes2.get(0));
    verify(abilityService).getOrCreate(abilities1.get(0));
    verify(attackService).getOrCreate(attacks1.get(0));
    verify(legalityService).getOrCreate(legalities1);
    verify(legalityService).getOrCreate(legalities2);
  }
}
