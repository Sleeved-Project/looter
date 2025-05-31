package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.Person;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonItemWriter implements ItemWriter<Person> {

  @Override
  public void write(Chunk<? extends Person> chunk) throws Exception {
    log.info("WRITER - Chunk size" + chunk.size() + " persons:");
    for (Person person : chunk) {
      log.info("WRITER - Person saved: " + person);
    }
  }
}