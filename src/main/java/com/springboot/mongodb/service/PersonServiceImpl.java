package com.springboot.mongodb.service;

import com.springboot.mongodb.collection.Person;
import com.springboot.mongodb.repository.PersonRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Person person) {

        return personRepository.save(person).getPersonId();
    }


    @Override
    public List<Person> getPersonStartsWith(String name) {

        return personRepository.findByFirstNameStartsWith(name);
    }

    @Override
    public void delete(String id) {

        personRepository.deleteById(id);
    }

    @Override
    public List<Person> getByPersonAge(Integer minAge, Integer maxAge) {
        return personRepository.findPersonByAgeBetween(minAge, maxAge);
    }

    @Override
    public List<Person> customFindByAgeBetween(Integer min, Integer max) {

        System.out.println("1. ############## @customFindByAgeBetween::  personRepository:: " +  personRepository);

        List<Person> persons = null;
         // persons = personRepository.findByAgeBetween(min, max);
//        System.out.println("1. ############## @customFindByAgeBetween::  persons:: " +  persons);
        persons = personRepository.findPersonByAgeBetween(min, max);
        System.out.println("2. ############## @customFindByAgeBetween::  persons:: " +  persons);

//        Query query = new Query();
//        query.addCriteria(Criteria.where("age").gt(min).lt(max));
//        // Ensure no default sorting is applied here
//        Sort sort = Sort.by("age");
//
//        System.out.println("############## @customFindByAgeBetween::  query.isSorted():: " +  query.isSorted());
//
//        System.out.println("############## @customFindByAgeBetween::  query:: " +  query.toString());
//        persons = mongoTemplate.find(query, Person.class);
//        System.out.println("3. ############## @customFindByAgeBetween::  persons:: " +  persons);
//        Aggregation aggregation = Aggregation.newAggregation(
//                Aggregation.match(Criteria.where("age").gt(min).lt(max))
//                // No sorting is applied in the aggregation pipeline
//        );
//        List<Person> result = mongoTemplate.aggregate(aggregation, "person", Person.class).getMappedResults();
        return persons;
    }

    @Override
    public Page<Person> searchPerson(String name, Integer minAge, Integer maxAge, String city, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Query query =  new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        if(name != null && !name.isEmpty())
        {
            criteria.add(Criteria.where("firstName").regex(name, "i") );
        }

        if(minAge != null && maxAge != null)
        {
            criteria.add(Criteria.where("age").gte(minAge).lte(maxAge));
        }

        if(city != null && !city.isEmpty())
        {
            criteria.add(Criteria.where("addresses.city").is(city));
        }

        if(!criteria.isEmpty())
        {
            query.addCriteria(new Criteria()
                    .andOperator(criteria.toArray(new Criteria[0])));
        }
        Page<Person> people = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Person.class),
                pageable, () -> mongoTemplate.count(query.skip(0).limit(0), Person.class)
        ) ;
        System.out.println("Search results: " + people);
        return people;
    }
//
//    024-10-19T13:35:22.407+05:30  WARN 61449 --- [Spring-Boot-Mongo-DB] [nio-8085-exec-5] ration$PageModule$WarningLoggingModifier : Serializing PageImpl instances as-is is not supported, meaning that there is no guarantee about the stability of the resulting JSON structure!
//    For a stable JSON structure, please use Spring Data's PagedModel (globally via @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO))
//    or Spring HATEOAS and Spring Data's PagedResourcesAssembler as documented in https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.pageables.


    @Override
    public List<Document> getOldestPerson() {
        // unwind operation:
        UnwindOperation unwindOperation = Aggregation.unwind("addresses");
        // Sort operation:
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC, "age");

        // Group operation:
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .first(Aggregation.ROOT)
                .as("oldestPerson");

        // Aggregation operation:
        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation, sortOperation, groupOperation);

        List<Document> persons
                = mongoTemplate.aggregate(aggregation, Person.class, Document.class).getMappedResults();
        System.out.println("Oldest persons = " + persons);
        return persons;
    }

    @Override
    public List<Document> getPopulationByCity() {
        // unwind operation:
        UnwindOperation unwindOperation = Aggregation.unwind("addresses");

        // Group operation:
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .count().as("popCount");

        // Sort operation:
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC, "popCount");

        ProjectionOperation projectionOperation
                = Aggregation.project()
                .andExpression("_id").as("city")
                .andExpression("popCount").as("count")
                .andExclude("_id");

        // Aggregation operation:
        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation, groupOperation, sortOperation, projectionOperation);

        List<Document> documents
                = mongoTemplate.aggregate(aggregation, Person.class, Document.class).getMappedResults();
        System.out.println("@getPopulationByCity():: documents = " + documents);
        return documents;
    }
}