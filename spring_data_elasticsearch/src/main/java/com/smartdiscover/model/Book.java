package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.stream.Collectors;

@Data
@Document(indexName = "educative")
public class Book {

    @Id
    private String id;

    private String name;
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
