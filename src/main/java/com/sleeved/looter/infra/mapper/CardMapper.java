package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.ParseUtil;
import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.infra.dto.CardDTO;

@Component
public class CardMapper {
  public Card toEntity(CardDTO cardDTO, Artist cardArtist, Rarity cardRarity, Set cardSet, Legalities cardLegatities) {
    Card card = new Card();
    card.setId(cardDTO.getId());
    card.setName(cardDTO.getName());
    card.setSupertype(cardDTO.getSupertype());
    card.setLevel(cardDTO.getLevel());
    card.setHp(cardDTO.getHp());
    card.setEvolvesTo(cardDTO.getEvolvesTo() == null ? null : String.join(", ", cardDTO.getEvolvesTo()));
    card.setConvertedRetreatCost(cardDTO.getConvertedRetreatCost() == null ? 0 : cardDTO.getConvertedRetreatCost());
    card.setNumber(cardDTO.getNumber());
    card.setImageLarge(cardDTO.getImages().getLarge());
    card.setImageSmall(cardDTO.getImages().getSmall());
    card.setFlavorText(cardDTO.getFlavorText());
    card.setNationalPokedexNumbers(cardDTO.getNationalPokedexNumbers() == null ? null
        : String.join(", ", ParseUtil.parseIntegerListIntoStringList(cardDTO.getNationalPokedexNumbers())));
    card.setArtist(cardArtist);
    card.setLegalities(cardLegatities);
    card.setRarity(cardRarity);
    card.setSet(cardSet);
    return card;
  }
}
