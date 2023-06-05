package com.smartdiscover.repository;

import com.smartdiscover.document.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, String> {
}