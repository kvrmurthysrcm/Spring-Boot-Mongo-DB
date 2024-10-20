package com.springboot.mongodb.repository;

import com.springboot.mongodb.collection.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {

    List<Person> findByFirstNameStartsWith(String name);

    // This is factory implementation
    List<Person> findByAgeBetween(Integer min, Integer max);

    // Using Query annotation
    //@Query( value = "{'age' : {$gt: ?0, $lt: ?1}}" )
    // @Query( "{'age' : {$gt: ?0, $lt: ?1}}" )
    @Query(value = "{ 'age' : { $gt: ?0, $lt: ?1 } }", sort = "{}",
        fields="{'personId': 1, 'firstName': 1, 'lastName': 1, 'age': 1, 'hobbies': 1}")
    List<Person> findPersonByAgeBetween(Integer min, Integer max);

}