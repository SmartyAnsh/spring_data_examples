package com.smartdiscover.repository;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.smartdiscover.model.Book;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CouchbaseRepository<Book, String> {

    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    Book findByName(String name);

}
