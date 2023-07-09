package com.smartdiscover.repositories;

import com.smartdiscover.model.Author;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends ElasticsearchRepository<Author, String> {

    List<Author> findAll();

}
