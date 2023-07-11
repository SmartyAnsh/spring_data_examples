package com.smartdiscover.repositories;

import com.smartdiscover.model.Book;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactiveBookRepository extends ReactiveElasticsearchRepository<Book, String> {

}
