package com.smartdiscover.repository;

import com.smartdiscover.model.Book;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface BookRepository extends CassandraRepository<Book, String> {
}