package com.smartdiscover.repositories;

import com.smartdiscover.model.Author;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactiveAuthorRepository extends ReactiveElasticsearchRepository<Author, String> {

}
