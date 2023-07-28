package com.smartdiscover.repository;

import com.smartdiscover.model.BookRating;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRatingRepository extends Neo4jRepository<BookRating, String> {
}
