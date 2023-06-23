package com.smartdiscover.repository;

import com.smartdiscover.model.Author;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveAuthorRepository extends ReactiveCouchbaseRepository<Author, String> {
}
