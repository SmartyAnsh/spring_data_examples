package com.smartdiscover.repository;

import com.smartdiscover.document.Author;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReactiveAuthorRepository extends ReactiveMongoRepository<Author, String> {
}