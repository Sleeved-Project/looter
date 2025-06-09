package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.repository.iris.HashCardRepository;

@Service
public class HashCardService {
    
    private final HashCardRepository hashCardRepository;

  public HashCardService(HashCardRepository hashCardRepository) {
    this.hashCardRepository = hashCardRepository;
  }

  public HashCard getOrCreate(HashCard hashCard) {
    return hashCardRepository.findByCardId(hashCard.getId())
        .orElseGet(() -> hashCardRepository.save(hashCard));
  }
  
  public HashCard getByCardId(String cardId) {
    return hashCardRepository.findByCardId(cardId)
        .orElse(null);
  }

}
