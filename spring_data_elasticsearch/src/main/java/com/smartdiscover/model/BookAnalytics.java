package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "bookanalytics")
public class BookAnalytics {

    @Id
    private String id;

    private Book book;

    private long borrowedCount;

    private long viewedCount;

    public BookAnalytics(Book book) {
        this.book = book;
        this.borrowedCount = 0;
        this.viewedCount = 0;
    }

    @Override
    public String toString() {
        return "BookAnalytics{" +
                "id=" + id +
                ", book='" + book.getName() + '\'' +
                ", borrowedCount=" + borrowedCount +
                ", viewedCount=" + viewedCount +
                '}';
    }

    public void incrementBorrowedCount() {
        this.borrowedCount += 1;
    }

    public void incrementViewedCount() {
        this.viewedCount += 1;
    }

}