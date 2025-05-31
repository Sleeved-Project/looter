package com.sleeved.looter.batch.reader;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.Person;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonItemReader implements ItemReader<Person> {

  private Iterator<Person> peopleIterator;

  private List<Person> people = Arrays.asList(
      new Person("Jean", "Dupont", 30),
      new Person("Marie", "Martin", 25),
      new Person("Pierre", "Bernard", 40));

  @Override
  public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    log.info("READER - Reading a person");
    if (peopleIterator == null) {
      peopleIterator = people.iterator();
    }

    if (peopleIterator.hasNext()) {
      return peopleIterator.next();
    } else {
      return null; // End of input
    }
  }
}