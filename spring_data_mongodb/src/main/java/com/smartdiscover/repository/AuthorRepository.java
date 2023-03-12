package com.smartdiscover.repository;

import com.smartdiscover.document.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorRepository extends MongoRepository<Author, String> {
}