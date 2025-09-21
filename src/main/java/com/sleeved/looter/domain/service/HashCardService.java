package com.sleeved.looter.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.repository.iris.HashCardRepository;

@Service
public class HashCardService {

  private final HashCardRepository hashCardRepository;

  public HashCardService(HashCardRepository hashCardRepository) {
    this.hashCardRepository = hashCardRepository;
  }

  @Transactional
  public HashCard save(HashCard hashCard) {
    return hashCardRepository.save(hashCard);
  }

  @Transactional
  public List<HashCard> saveAll(List<HashCard> hashCards) {
    return hashCardRepository.saveAll(hashCards);
  }
}