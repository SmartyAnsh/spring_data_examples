package com.smartdiscover.repository;

import com.smartdiscover.entity.Person;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomPersonRepository {

    Person findPersonCustom(String firstName, String lastName);

}