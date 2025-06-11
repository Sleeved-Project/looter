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
    return hashCardRepository.findById(hashCard.getId())
        .orElseGet(() -> hashCardRepository.save(hashCard));
  }

}
