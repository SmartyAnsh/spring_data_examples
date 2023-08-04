package com.smartdiscover.repository;

import com.smartdiscover.model.Author;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReactiveAuthorRepository extends ReactiveCassandraRepository<Author, UUID> {
}
