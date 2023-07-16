package com.smartdiscover.repository;

import com.smartdiscover.entity.BookLoanEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLoanEntryRepository extends JpaRepository<BookLoanEntry, Long> {

}
