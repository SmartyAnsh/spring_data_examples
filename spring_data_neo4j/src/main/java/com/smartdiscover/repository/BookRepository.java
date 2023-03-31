package com.smartdiscover.repository;

import com.smartdiscover.model.Book;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends Neo4jRepository<Book, Long> {
}
