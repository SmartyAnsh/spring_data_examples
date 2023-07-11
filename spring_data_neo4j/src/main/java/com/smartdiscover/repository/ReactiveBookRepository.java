package com.smartdiscover.repository;

import com.smartdiscover.model.Book;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveBookRepository extends ReactiveNeo4jRepository<Book, String> {
}
