package com.smartdiscover.repository;

import com.smartdiscover.entity.Person;
import com.smartdiscover.entity.projection.PersonFullName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long>, CustomPersonRepository, QuerydslPredicateExecutor<Person> {

    List<Person> findAllByFirstName(String firstName);

    Optional<Person> findFirstByLastNameOrderByFirstNameDesc(String lastName);

    Person findFirstByOrderByFirstNameAsc();

    Stream<Person> readAllByOrderById();

    @Query("select p from Person p order by p.id asc limit 1")
    Person fetchFirstPerson();

    PersonFullName findFirstByOrderByFirstNameDesc();
}