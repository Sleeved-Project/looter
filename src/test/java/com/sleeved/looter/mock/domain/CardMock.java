package com.sleeved.looter.mock.domain;

import java.util.ArrayList;
import java.util.List;

import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Set;

public class CardMock {

  public static Card createMockCard(String id, String name, String supertype, String level,
      String hp, String evolvesTo, Integer convertedRetreatCost,
      String number, String imageLarge, String imageSmall,
      String flavorText, String nationalPokedexNumbers,
      Artist artist, Rarity rarity, Set set, Legalities legalities) {
    Card card = new Card();
    card.setId(id);
    card.setName(name);
    card.setSupertype(supertype);
    card.setLevel(level);
    card.setHp(hp);
    card.setEvolvesTo(evolvesTo);
    card.setConvertedRetreatCost(convertedRetreatCost);
    card.setNumber(number);
    card.setImageLarge(imageLarge);
    card.setImageSmall(imageSmall);
    card.setFlavorText(flavorText);
    card.setNationalPokedexNumbers(nationalPokedexNumbers);
    card.setArtist(artist);
    card.setRarity(rarity);
    card.setSet(set);
    card.setLegalities(legalities);
    return card;
  }

  public static Card createMockCardSavedInDb(Integer dbId, String id, String name, String supertype,
      String level, String hp, String evolvesTo,
      Integer convertedRetreatCost, String number,
      String imageLarge, String imageSmall,
      String flavorText, String nationalPokedexNumbers,
      Artist artist, Rarity rarity, Set set, Legalities legalities) {
    Card card = createMockCard(id, name, supertype, level, hp, evolvesTo, convertedRetreatCost,
        number, imageLarge, imageSmall, flavorText, nationalPokedexNumbers,
        artist, rarity, set, legalities);
    return card;
  }

  public static Card createBasicMockCard(String id, String name) {
    return createMockCard(
        id,
        name,
        "Pokémon",
        null,
        "70",
        null,
        1,
        "1",
        "https://example.com/large.png",
        "https://example.com/small.png",
        "A simple Pokémon card.",
        "25",
        null,
        null,
        null,
        null);
  }

  public static List<Card> createMockCardsWithImage(int count) {
    List<Card> cards = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Card card = new Card();
      card.setId("card-" + (i + 1));
      card.setImageLarge("http://example.com/card" + (i + 1) + ".jpg");
      cards.add(card);
    }
    return cards;
  }

  public static Card createMockCardWithoutImage() {
    Card card = new Card();
    card.setId("card-no-image");
    card.setImageLarge(null);
    return card;
  }

}