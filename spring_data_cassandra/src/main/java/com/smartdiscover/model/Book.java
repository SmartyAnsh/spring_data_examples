package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@Table(value = "book")
public class Book {

    @PrimaryKeyColumn(name = "id", ordinal = 2,
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING)
    private UUID id;

    @PrimaryKeyColumn(name = "name", ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private String name;

    @PrimaryKeyColumn(name = "summary", ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private String summary;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}