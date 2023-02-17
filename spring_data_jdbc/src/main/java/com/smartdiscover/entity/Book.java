package com.smartdiscover.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Data
@Table(name = "BOOK")
public class Book {

    @Id
    private long id;

    private String name;
    private String summary;

    @MappedCollection(idColumn = "BOOK_ID")
    private Set<AuthorBook> authors = new HashSet<>();

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", authors=" + authors.stream().mapToLong(i -> i.getAuthorId()).boxed().toList().toString() +
                '}';
    }
}
