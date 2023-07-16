package com.smartdiscover.repository;

import com.smartdiscover.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByNameAndAvailableIsNullOrAvailableIsTrue(String name);

    Book findFirstBySummaryIsLikeOrderByNameAsc(String summary);
}