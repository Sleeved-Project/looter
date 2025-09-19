package com.sleeved.looter.batch.writer;

import java.util.stream.Collectors;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.service.HashCardService;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HashImageWriter implements ItemWriter<HashCard> {

  private final HashCardService hashCardService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public HashImageWriter(
      HashCardService hashCardService,
      LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.hashCardService = hashCardService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends HashCard> chunk) {
    try {
      if (!chunk.isEmpty()) {
        hashCardService.saveAll(chunk.getItems().stream().collect(Collectors.toList()));
      }
    } catch (Exception e) {
      log.warn("Batch save failed, falling back to individual saves", e);
      writeIndividually(chunk);
    }
  }

  private void writeIndividually(Chunk<? extends HashCard> chunk) {
    for (HashCard hashCard : chunk) {
      try {
        if (hashCard != null && hashCard.getHash() != null) {
          hashCardService.save(hashCard);
        }
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.HASH_IMAGE_ITEM,
            hashCard.toString());
        looterScrapingErrorHandler.handle(e,
            Constantes.HASH_IMAGE_WRITER_CONTEXT,
            Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}