package com.smartdiscover.repository;

import com.smartdiscover.model.BookAnalytics;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookAnalyticsRepository extends CassandraRepository<BookAnalytics, UUID> {

    @AllowFiltering
    BookAnalytics findByBookName(String bookName);

}
