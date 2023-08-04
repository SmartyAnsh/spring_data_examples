package com.smartdiscover.repository;

import com.smartdiscover.model.BookByAuthor;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookByAuthorRepository extends CassandraRepository<BookByAuthor, UUID> {
}
