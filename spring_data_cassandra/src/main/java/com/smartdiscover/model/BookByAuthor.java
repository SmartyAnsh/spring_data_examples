package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@Table
public class BookByAuthor {

    @PrimaryKey
    private UUID id;

    @Column
    private String bookName;

    @Column
    private List<String> authorNames;

}