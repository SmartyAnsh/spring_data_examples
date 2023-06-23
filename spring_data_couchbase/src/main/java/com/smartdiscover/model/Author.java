package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Document
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;
    private String firstName;
    private String lastName;

    private List<Book> books;

    public String getFullName() {
        return lastName + " " + firstName;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ((null != books) ? ", books=" + books.stream().map(i -> i.getName()).collect(Collectors.toList()) + '\'' : "") +
                '}';
    }

}
