package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.service.HashCardService;
import com.sleeved.looter.infra.dto.HashImageDTO;
import com.sleeved.looter.infra.mapper.HashCardMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HashImageWriter implements ItemWriter<HashImageDTO> {

  private final HashCardService hashCardService;
  private final HashCardMapper hashCardMapper;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public HashImageWriter(
      HashCardService hashCardService,
      HashCardMapper hashCardMapper,
      LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.hashCardService = hashCardService;
    this.hashCardMapper = hashCardMapper;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends HashImageDTO> chunk) throws Exception {
    for (HashImageDTO hashImageDTO : chunk) {
      try {
        if (hashImageDTO == null || hashImageDTO.getHash() == null) {
          continue;
        }

        HashCard hashCard = hashCardMapper.toEntity(hashImageDTO);

        hashCardService.getOrCreate(hashCard);
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.CARD_ENTITIES_ITEM,
            hashImageDTO.toString());
        looterScrapingErrorHandler.handle(e, Constantes.HASH_IMAGE_WRITER_CONTEXT, Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}
