package com.smartdiscover.repository;

import com.smartdiscover.document.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, String> {
}