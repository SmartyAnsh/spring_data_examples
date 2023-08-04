package com.smartdiscover.repository;

import com.smartdiscover.model.Author;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorRepository extends CassandraRepository<Author, UUID> {
}
