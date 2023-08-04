package com.smartdiscover.repository;

import com.smartdiscover.model.BookByAuthor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookByAuthorRepository extends CassandraRepository<BookByAuthor, UUID> {
}
