package com.smartdiscover.repository;

import com.smartdiscover.document.BookLoanEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLoanEntryRepository extends MongoRepository<BookLoanEntry, String> {

}
