package com.springboot.mongodb.service;

import com.springboot.mongodb.collection.Person;
import org.bson.Document;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonService {


    String save(Person person);

    List<Person> getPersonStartsWith(String name);

    void delete(String id);

    List<Person> getByPersonAge(Integer minAge, Integer maxAge);

    List<Person> customFindByAgeBetween(Integer min, Integer max);

    Page<Person> searchPerson(String name, Integer minAge, Integer maxAge, String city, Integer page, Integer size);

    List<Document> getOldestPerson();

    List<Document> getPopulationByCity();
}
