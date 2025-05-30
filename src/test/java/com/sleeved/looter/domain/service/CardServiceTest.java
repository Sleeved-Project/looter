package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.repository.atlas.CardRepository;
import com.sleeved.looter.mock.domain.ArtistMock;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.RarityMock;
import com.sleeved.looter.mock.domain.SetMock;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

  @Mock
  private CardRepository cardRepository;

  @InjectMocks
  private CardService cardService;

  @Test
  void getOrCreate_shouldReturnExistingCard_whenCardExists() {
    Artist artist = ArtistMock.createMockArtistSavedInDb(1, "Ken Sugimori");
    Rarity rarity = RarityMock.createMockRaritySavedInDb(1, "Rare");
    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("base1", "Base", "Base Set", 102, 102, "BS",
        "symbol.png", "logo.png", legalities);

    Card inputCard = CardMock.createMockCard(
        "base1-4",
        "Charizard",
        "Pokémon",
        "76",
        "120",
        "Charizard GX, Charizard V",
        3,
        "4",
        "https://images.pokemontcg.io/base1/4_large.png",
        "https://images.pokemontcg.io/base1/4_small.png",
        "Spits fire that is hot enough to melt boulders.",
        "6",
        artist,
        rarity,
        set,
        legalities);

    Card existingCard = CardMock.createMockCard(
        "base1-4",
        "Charizard",
        "Pokémon",
        "76",
        "120",
        "Charizard GX, Charizard V",
        3,
        "4",
        "https://images.pokemontcg.io/base1/4_large.png",
        "https://images.pokemontcg.io/base1/4_small.png",
        "Spits fire that is hot enough to melt boulders.",
        "6",
        artist,
        rarity,
        set,
        legalities);

    when(cardRepository.findById("base1-4")).thenReturn(Optional.of(existingCard));

    Card result = cardService.getOrCreate(inputCard);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("base1-4");
    assertThat(result.getName()).isEqualTo("Charizard");
    assertThat(result.getSupertype()).isEqualTo("Pokémon");
    assertThat(result.getLevel()).isEqualTo("76");
    assertThat(result.getHp()).isEqualTo("120");
    assertThat(result.getEvolvesTo()).isEqualTo("Charizard GX, Charizard V");
    assertThat(result.getConvertedRetreatCost()).isEqualTo(3);
    assertThat(result.getNumber()).isEqualTo("4");
    assertThat(result.getImageLarge()).isEqualTo("https://images.pokemontcg.io/base1/4_large.png");
    assertThat(result.getImageSmall()).isEqualTo("https://images.pokemontcg.io/base1/4_small.png");
    assertThat(result.getFlavorText()).isEqualTo("Spits fire that is hot enough to melt boulders.");
    assertThat(result.getNationalPokedexNumbers()).isEqualTo("6");
    assertThat(result.getArtist()).isEqualTo(artist);
    assertThat(result.getRarity()).isEqualTo(rarity);
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getLegalities()).isEqualTo(legalities);

    verify(cardRepository).findById("base1-4");
    verify(cardRepository, never()).save(any(Card.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCard_whenCardDoesNotExist() {
    Artist artist = ArtistMock.createMockArtistSavedInDb(2, "Mitsuhiro Arita");
    Rarity rarity = RarityMock.createMockRaritySavedInDb(2, "Uncommon");
    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(2, "Legal", "Legal", "Legal");
    Set set = SetMock.createMockSet("sm1", "Sun & Moon", "Sun & Moon", 149, 149, "SM1",
        "symbol-sm.png", "logo-sm.png", legalities);

    Card inputCard = CardMock.createMockCard(
        "sm1-25",
        "Pikachu",
        "Pokémon",
        null,
        "70",
        null,
        1,
        "25",
        "https://images.pokemontcg.io/sm1/25_large.png",
        "https://images.pokemontcg.io/sm1/25_small.png",
        "When several of these Pokémon gather, their electricity could build and cause lightning storms.",
        "25",
        artist,
        rarity,
        set,
        legalities);

    Card savedCard = CardMock.createMockCard(
        "sm1-25",
        "Pikachu",
        "Pokémon",
        null,
        "70",
        null,
        1,
        "25",
        "https://images.pokemontcg.io/sm1/25_large.png",
        "https://images.pokemontcg.io/sm1/25_small.png",
        "When several of these Pokémon gather, their electricity could build and cause lightning storms.",
        "25",
        artist,
        rarity,
        set,
        legalities);

    when(cardRepository.findById("sm1-25")).thenReturn(Optional.empty());
    when(cardRepository.save(inputCard)).thenReturn(savedCard);

    Card result = cardService.getOrCreate(inputCard);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("sm1-25");
    assertThat(result.getName()).isEqualTo("Pikachu");
    assertThat(result.getSupertype()).isEqualTo("Pokémon");
    assertThat(result.getLevel()).isNull();
    assertThat(result.getHp()).isEqualTo("70");
    assertThat(result.getEvolvesTo()).isNull();
    assertThat(result.getConvertedRetreatCost()).isEqualTo(1);
    assertThat(result.getNumber()).isEqualTo("25");
    assertThat(result.getImageLarge()).isEqualTo("https://images.pokemontcg.io/sm1/25_large.png");
    assertThat(result.getImageSmall()).isEqualTo("https://images.pokemontcg.io/sm1/25_small.png");
    assertThat(result.getFlavorText())
        .isEqualTo("When several of these Pokémon gather, their electricity could build and cause lightning storms.");
    assertThat(result.getNationalPokedexNumbers()).isEqualTo("25");
    assertThat(result.getArtist()).isEqualTo(artist);
    assertThat(result.getRarity()).isEqualTo(rarity);
    assertThat(result.getSet()).isEqualTo(set);
    assertThat(result.getLegalities()).isEqualTo(legalities);

    verify(cardRepository).findById("sm1-25");
    verify(cardRepository).save(inputCard);
  }

  @Test
  void getOrCreate_shouldHandleNullRelationships() {
    Card inputCard = CardMock.createBasicMockCard("swsh1-1", "Bulbasaur");
    Card savedCard = CardMock.createBasicMockCard("swsh1-1", "Bulbasaur");

    when(cardRepository.findById("swsh1-1")).thenReturn(Optional.empty());
    when(cardRepository.save(inputCard)).thenReturn(savedCard);

    Card result = cardService.getOrCreate(inputCard);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("swsh1-1");
    assertThat(result.getName()).isEqualTo("Bulbasaur");
    assertThat(result.getArtist()).isNull();
    assertThat(result.getRarity()).isNull();
    assertThat(result.getSet()).isNull();
    assertThat(result.getLegalities()).isNull();

    verify(cardRepository).findById("swsh1-1");
    verify(cardRepository).save(inputCard);
  }
}
