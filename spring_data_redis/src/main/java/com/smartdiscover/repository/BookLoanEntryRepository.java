package com.smartdiscover.repository;

import com.smartdiscover.model.BookLoanEntry;
import com.smartdiscover.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLoanEntryRepository extends CrudRepository<BookLoanEntry, String> {
}
