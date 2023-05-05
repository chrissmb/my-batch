package com.example.mybatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.example.mybatch.schema.Person;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	
	private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

	@Override
	public Person process(Person person) throws Exception {
		String name = person.getName().toUpperCase();
		Long id = person.getId();
		Person processedPerson = new Person(id, name);
		log.info("Processed person:{{}} - old person: {{}}", processedPerson, person);
		return processedPerson;
	}
}
