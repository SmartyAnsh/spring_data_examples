package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Data
@Node("BookRating")
public class BookRating {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    private int rating;

    public BookRating() {
    }

    public BookRating(int rating) {
        this.rating = rating;
    }

}
