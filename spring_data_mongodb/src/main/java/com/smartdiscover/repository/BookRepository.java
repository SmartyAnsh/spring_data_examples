package com.smartdiscover.repository;

import com.smartdiscover.document.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {

    Book findFirstByName(String name);

    List<Book> findAllByName(String name);

    Book findByNameAndAvailableIsNullOrAvailableIsTrue(String name);
}