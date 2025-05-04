package com.sleeved.looter.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.Person;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

  @Override
  public Person process(Person person) throws Exception {
    log.info("PROCESSOR - Converting person: " + person);
    // Exemple de traitement : Mettre le nom en majuscules
    Person transformedPerson = new Person();
    transformedPerson.setFirstName(person.getFirstName());
    transformedPerson.setLastName(person.getLastName().toUpperCase());
    transformedPerson.setAge(person.getAge());
    return transformedPerson;
  }
}