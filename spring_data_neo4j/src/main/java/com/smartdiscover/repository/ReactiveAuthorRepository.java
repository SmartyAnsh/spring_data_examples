package com.smartdiscover.repository;

import com.smartdiscover.model.Author;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveAuthorRepository extends ReactiveNeo4jRepository<Author, String> {
}
