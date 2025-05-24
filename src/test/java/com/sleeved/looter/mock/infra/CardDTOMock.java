package com.sleeved.looter.mock.infra;

import java.util.ArrayList;
import java.util.List;

import com.sleeved.looter.infra.dto.CardDTO;

public class CardDTOMock {

  public static CardDTO createMockCardDTO(String id, String name) {
    CardDTO card = new CardDTO();
    card.setId(id);
    card.setName(name);
    return card;
  }

  public static List<CardDTO> createMockCardDTOsList(int count) {
    List<CardDTO> cards = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      CardDTO card = createMockCardDTO("card-" + i, "Test Card " + i);
      cards.add(card);
    }

    return cards;
  }
}