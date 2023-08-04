package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@Table
public class BookAnalytics {

    @PrimaryKey
    private UUID bookId;

    @Column
    private String bookName;

    @Column
    private long borrowedCount;

    @Column
    private long viewedCount;

    @Column
    private double averageRating;

    @Column
    private long totalRatings;

    @Override
    public String toString() {
        return "BookAnalytics{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", borrowedCount='" + borrowedCount + '\'' +
                ", viewedCount='" + viewedCount + '\'' +
                ", averageRating='" + averageRating + '\'' +
                ", totalRatings='" + totalRatings + '\'' +
                '}';
    }

    public BookAnalytics(UUID bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.borrowedCount = 0;
        this.viewedCount = 0L;
        this.averageRating = 0D;
    }

    public void incrementBorrowedCount() {
        this.borrowedCount += 1;
    }

    public void incrementViewedCount() {
        this.viewedCount += 1;
    }

    public void incrementTotalRatings() {
        this.totalRatings += 1;
    }
}
