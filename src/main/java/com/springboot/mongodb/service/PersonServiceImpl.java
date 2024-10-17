package com.springboot.mongodb.service;

import com.springboot.mongodb.collection.Person;
import com.springboot.mongodb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Override
    public String save(Person person) {

        return personRepository.save(person).getPersonId();
    }
}