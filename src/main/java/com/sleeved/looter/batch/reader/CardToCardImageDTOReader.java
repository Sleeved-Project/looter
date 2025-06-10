package com.sleeved.looter.batch.reader;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.repository.atlas.CardRepository;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@StepScope
@Slf4j
public class CardToCardImageDTOReader implements ItemReader<CardImageDTO> {
    
    private Iterator<Card> cardIterator;
    private final CardRepository cardRepository;
    private final LooterScrapingErrorHandler looterScrapingErrorHandler;

    @Autowired
    public CardToCardImageDTOReader(CardRepository cardRepository, LooterScrapingErrorHandler looterScrapingErrorHandler) {
        this.cardRepository = cardRepository;
        this.looterScrapingErrorHandler = looterScrapingErrorHandler;
        
        List<Card> cards = this.cardRepository.findByImageLargeIsNotNull();
        this.cardIterator = cards.iterator();
    }
    
    /**
     * Reads the next card image from the repository.
     * 
     * @return CardImageDTO containing the card ID and image URL, or null if no more cards are available.
     */
    @Override
    public CardImageDTO read() {
        while (cardIterator.hasNext()) {
            Card card = cardIterator.next();
            try {
                if (card.getImageLarge() != null && !card.getImageLarge().isEmpty()) {
                    CardImageDTO imageDTO = new CardImageDTO();
                    imageDTO.setCardId(card.getId());
                    imageDTO.setImageUrl(card.getImageLarge());
                    
                    return imageDTO;
                }
                continue;
            } catch (Exception e) {
                String formatedItem = looterScrapingErrorHandler.formatErrorItem(
                    Constantes.CARD_IMAGE_DTO_ITEM,
                    card.getId());
                looterScrapingErrorHandler.handle(e, Constantes.CARD_IMAGE_READER_CONTEXT, Constantes.READER_ACTION,
                        formatedItem);
                continue;
            }
        }
        return null;
    }
}