package com.smartdiscover.repository;

import com.smartdiscover.model.Author;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CouchbaseRepository<Author, String> {
}
