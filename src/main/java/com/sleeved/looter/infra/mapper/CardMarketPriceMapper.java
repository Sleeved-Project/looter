package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.infra.dto.CardMarketDTO;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.common.util.DateUtil;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;

@Component
public class CardMarketPriceMapper {
  public CardMarketPrice toEntity(CardMarketDTO cardMarketDTO, Card card) {
    if (cardMarketDTO == null || card == null) {
      return null;
    }
    CardMarketPrice cardMarketPrice = new CardMarketPrice();
    cardMarketPrice.setUrl(cardMarketDTO.getUrl());
    cardMarketPrice.setUpdatedAt(DateUtil.parseDate(cardMarketDTO.getUpdatedAt(), Constantes.STAGING_CARD_DATE_FORMAT));
    cardMarketPrice.setCard(card);
    if (cardMarketDTO.getPrices() == null) {
      return cardMarketPrice;
    }
    cardMarketPrice.setAverageSellPrice(cardMarketDTO.getPrices().getAverageSellPrice());
    cardMarketPrice.setAvg1(cardMarketDTO.getPrices().getAvg1());
    cardMarketPrice.setAvg7(cardMarketDTO.getPrices().getAvg7());
    cardMarketPrice.setAvg30(cardMarketDTO.getPrices().getAvg30());
    cardMarketPrice.setGermanProLow(cardMarketDTO.getPrices().getGermanProLow());
    cardMarketPrice.setLowPrice(cardMarketDTO.getPrices().getLowPrice());
    cardMarketPrice.setLowPriceExPlus(cardMarketDTO.getPrices().getLowPriceExPlus());
    cardMarketPrice.setReverseHoloAvg1(cardMarketDTO.getPrices().getReverseHoloAvg1());
    cardMarketPrice.setReverseHoloAvg7(cardMarketDTO.getPrices().getReverseHoloAvg7());
    cardMarketPrice.setReverseHoloAvg30(cardMarketDTO.getPrices().getReverseHoloAvg30());
    cardMarketPrice.setReverseHoloLow(cardMarketDTO.getPrices().getReverseHoloLow());
    cardMarketPrice.setReverseHoloSell(cardMarketDTO.getPrices().getReverseHoloSell());
    cardMarketPrice.setReverseHoloTrend(cardMarketDTO.getPrices().getReverseHoloTrend());
    cardMarketPrice.setSuggestedPrice(cardMarketDTO.getPrices().getSuggestedPrice());
    cardMarketPrice.setTrendPrice(cardMarketDTO.getPrices().getTrendPrice());
    return cardMarketPrice;
  }
}
