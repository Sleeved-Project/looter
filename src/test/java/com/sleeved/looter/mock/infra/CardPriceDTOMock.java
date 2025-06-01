package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.CardMarketDTO;
import com.sleeved.looter.infra.dto.CardPriceDTO;
import com.sleeved.looter.infra.dto.TcgPlayerDTO;

public class CardPriceDTOMock {

  public static CardPriceDTO createMockCardPriceDTO(String id, String name, TcgPlayerDTO tcgplayer,
      CardMarketDTO cardmarket) {
    CardPriceDTO dto = new CardPriceDTO();
    dto.setId(id);
    dto.setName(name);
    dto.setTcgplayer(tcgplayer);
    dto.setCardmarket(cardmarket);
    return dto;
  }

  public static CardPriceDTO createBasicMockCardPriceDTO(String id, String name) {
    TcgPlayerDTO tcgplayer = TcgPlayerDTOMock.createMockTcgPlayerDTO(
        "https://tcgplayer.com/" + id,
        "2023/05/15");

    CardMarketDTO cardmarket = CardMarketDTOMock.createMockCardMarketDTO(
        "https://cardmarket.com/" + id,
        "2023/05/15");

    return createMockCardPriceDTO(id, name, tcgplayer, cardmarket);
  }
}