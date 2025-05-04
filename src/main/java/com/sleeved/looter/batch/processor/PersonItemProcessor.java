package com.sleeved.looter.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.Person;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
  @Autowired
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @Override
  public Person process(Person person) throws Exception {
    log.info("PROCESSOR - Converting person: " + person);
    // Exemple de traitement : Mettre le nom en majuscules
    try {
      Person transformedPerson = new Person();
      transformedPerson.setFirstName(person.getFirstName());
      transformedPerson.setLastName(person.getLastName().toUpperCase());
      transformedPerson.setAge(person.getAge());
      return transformedPerson;
    } catch (Exception e) {
      looterScrapingErrorHandler.handle(e, Constantes.PROCESSOR_CONTEXT, Constantes.UPDATE_ACTION, person.toString());
      return null; // Ne sera jamais atteint grâce à l'exception lancée
    }

  }
}