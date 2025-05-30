package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.ImageDTO;
import com.sleeved.looter.mock.domain.ArtistMock;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.RarityMock;
import com.sleeved.looter.mock.domain.SetMock;

@ExtendWith(MockitoExtension.class)
public class CardMapperTest {

  @InjectMocks
  private CardMapper mapper;

  private CardDTO cardDTO;
  private Artist artist;
  private Rarity rarity;
  private Set set;
  private Legalities legalities;

  @BeforeEach
  void setUp() {
    artist = ArtistMock.createMockArtist("Mitsuhiro Arita");
    rarity = RarityMock.createMockRarity("Rare");
    legalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    set = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS", "symbol.png", "logo.png", legalities);

    ImageDTO imagesDTO = new ImageDTO();
    imagesDTO.setSmall("https://images.pokemontcg.io/base1/4_small.png");
    imagesDTO.setLarge("https://images.pokemontcg.io/base1/4_large.png");

    cardDTO = new CardDTO();
    cardDTO.setId("base1-4");
    cardDTO.setName("Charizard");
    cardDTO.setSupertype("Pokémon");
    cardDTO.setLevel("76");
    cardDTO.setHp("120");
    cardDTO.setEvolvesTo(Arrays.asList("Charizard GX", "Charizard V"));
    cardDTO.setConvertedRetreatCost(3);
    cardDTO.setNumber("4");
    cardDTO.setImages(imagesDTO);
    cardDTO
        .setFlavorText("Spits fire that is hot enough to melt boulders. Known to cause forest fires unintentionally.");
    cardDTO.setNationalPokedexNumbers(Arrays.asList(6));
  }

  @Test
  void toEntity_shouldMapValidDTO() {
    Card result = mapper.toEntity(cardDTO, artist, rarity, set, legalities);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("base1-4");
    assertThat(result.getName()).isEqualTo("Charizard");
    assertThat(result.getSupertype()).isEqualTo("Pokémon");
    assertThat(result.getLevel()).isEqualTo("76");
    assertThat(result.getHp()).isEqualTo("120");
    assertThat(result.getEvolvesTo()).isEqualTo("Charizard GX, Charizard V");
    assertThat(result.getConvertedRetreatCost()).isEqualTo(3);
    assertThat(result.getNumber()).isEqualTo("4");
    assertThat(result.getImageSmall()).isEqualTo("https://images.pokemontcg.io/base1/4_small.png");
    assertThat(result.getImageLarge()).isEqualTo("https://images.pokemontcg.io/base1/4_large.png");
    assertThat(result.getFlavorText())
        .isEqualTo("Spits fire that is hot enough to melt boulders. Known to cause forest fires unintentionally.");
    assertThat(result.getNationalPokedexNumbers()).isEqualTo("6");
    assertThat(result.getArtist()).isEqualTo(artist);
    assertThat(result.getRarity()).isEqualTo(rarity);
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getLegalities()).isEqualTo(legalities);
  }

  @Test
  void toEntity_shouldHandleNullEvolvesTo() {
    cardDTO.setEvolvesTo(null);

    Card result = mapper.toEntity(cardDTO, artist, rarity, set, legalities);

    assertThat(result).isNotNull();
    assertThat(result.getEvolvesTo()).isNull();
  }

  @Test
  void toEntity_shouldHandleNullConvertedRetreatCost() {
    cardDTO.setConvertedRetreatCost(null);

    Card result = mapper.toEntity(cardDTO, artist, rarity, set, legalities);

    assertThat(result).isNotNull();
    assertThat(result.getConvertedRetreatCost()).isEqualTo(0);
  }

  @Test
  void toEntity_shouldHandleNullNationalPokedexNumbers() {
    cardDTO.setNationalPokedexNumbers(null);

    Card result = mapper.toEntity(cardDTO, artist, rarity, set, legalities);

    assertThat(result).isNotNull();
    assertThat(result.getNationalPokedexNumbers()).isNull();
  }

  @Test
  void toEntity_shouldHandleMultipleNationalPokedexNumbers() {
    cardDTO.setNationalPokedexNumbers(Arrays.asList(6, 7, 8));

    Card result = mapper.toEntity(cardDTO, artist, rarity, set, legalities);

    assertThat(result).isNotNull();
    assertThat(result.getNationalPokedexNumbers()).isEqualTo("6, 7, 8");
  }

  @Test
  void toEntity_shouldHandleNullRelationships() {
    Card result = mapper.toEntity(cardDTO, null, null, null, null);

    assertThat(result).isNotNull();
    assertThat(result.getArtist()).isNull();
    assertThat(result.getRarity()).isNull();
    assertThat(result.getSet()).isNull();
    assertThat(result.getLegalities()).isNull();
  }
}
