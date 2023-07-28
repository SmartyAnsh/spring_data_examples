package com.smartdiscover.repository;

import com.smartdiscover.model.Genre;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends Neo4jRepository<Genre, String> {

    Genre findByGenreText(String genreText);

}
