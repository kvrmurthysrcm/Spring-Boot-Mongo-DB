package com.springboot.mongodb.controller;

import com.springboot.mongodb.collection.Person;
import com.springboot.mongodb.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.bson.Document;
import java.util.List;

@RestController
@RequestMapping("/person")
@Tag(name = "Person API", description = "Person API with MongoDB CRUD Demo")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    @Operation(summary = "Save the Person and return ID:: save(@RequestBody Person person)", description = "")
    public String save(@RequestBody Person person){

        return personService.save(person);
    }

    @GetMapping
    @Operation(summary = "List<Person> getPersonStartWith(@RequestParam(\"name\") String name)", description = "")
    public List<Person> getPersonStartWith(@RequestParam("name") String name){

        return personService.getPersonStartsWith(name);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Person using ID:: delete(@PathVariable(\"name\") String id)", description = "")
    public void delete(@PathVariable("name") String id){

        personService.delete(id);
    }

    @GetMapping("/age")
    @Operation(summary = "List<Person> getByPersonAge(Integer minAge, Integer maxAge)")
    public List<Person> getByPersonAge(@RequestParam("minAge") Integer minAge,
                                       @RequestParam("maxAge") Integer maxAge){
        // return personService.getByPersonAge(minAge, maxAge);
        return personService.customFindByAgeBetween(minAge, maxAge);
    }


    @GetMapping("/search")
    @Operation(summary = "List<Person> getByPersonAge( String name, Integer minAge, Integer maxAge, String city,Integer page,Integer size)")
    public Page<Person> searchPerson(@RequestParam(required=false) String name,
                                     @RequestParam(required=false) Integer minAge,
                                     @RequestParam(required=false) Integer maxAge,
                                     @RequestParam(required=false) String city,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "5") Integer size  ){

        return personService.searchPerson(name, minAge, maxAge, city, page, size);
    }

    @GetMapping("/oldestPerson")
    public List<Document> getOldestPerson(){

        return personService.getOldestPerson();
    }

    @GetMapping("/populationByCity")
    public List<Document> getPopulationByCity(){

        return personService.getPopulationByCity();
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint:: ResponseEntity<String> testEndpoint()", description = "")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test endpoint is working!");
    }


}