package com.smartdiscover.repository;

import com.smartdiscover.model.BookReview;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReviewRepository extends CouchbaseRepository<BookReview, String> {

}
