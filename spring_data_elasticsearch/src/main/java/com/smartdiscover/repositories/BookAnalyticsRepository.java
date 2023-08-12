package com.smartdiscover.repositories;

import com.smartdiscover.model.BookAnalytics;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookAnalyticsRepository extends ElasticsearchRepository<BookAnalytics, String> {

    BookAnalytics findByBook_Id(String bookId);

    List<BookAnalytics> findTop3ByOrderByBorrowedCountDescViewedCountDesc();

}
