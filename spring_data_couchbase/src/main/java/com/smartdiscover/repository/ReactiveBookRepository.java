package com.smartdiscover.repository;

import com.smartdiscover.model.Book;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveBookRepository extends ReactiveCouchbaseRepository<Book, String> {
}
