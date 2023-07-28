package com.smartdiscover.repository;

import com.smartdiscover.model.Book;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends Neo4jRepository<Book, String> {

    List<Book> findAllByGenres_Id(String id);

}
