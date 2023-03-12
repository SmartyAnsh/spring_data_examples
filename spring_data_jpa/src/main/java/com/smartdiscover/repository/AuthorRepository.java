package com.smartdiscover.repository;

import com.smartdiscover.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAllByFirstNameAndLastName(String firstName, String lastName);
    
}