package com.smartdiscover.repository;

import com.smartdiscover.model.Book;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends SolrCrudRepository<Book, String> {
}