package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.CardImageDTO;

public class CardImageDTOMock {

    public static CardImageDTO createCardImageDTO(String imageUrl) {
        CardImageDTO cardImageDTO = new CardImageDTO();
        cardImageDTO.setCardId("card-image-id");
        cardImageDTO.setImageUrl(imageUrl);
        return cardImageDTO;
    }
}
