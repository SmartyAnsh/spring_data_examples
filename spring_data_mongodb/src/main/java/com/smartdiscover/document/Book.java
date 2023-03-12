package com.smartdiscover.document;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Document
public class Book {

    @Id
    private String id;

    private String name;

    private String summary;

    @DBRef
    private List<Author> authors;

    @CreatedDate
    private Date dateCreated;

    @CreatedBy
    private String createdBy;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ((null != authors) ? ", authors=" + authors.stream().map(i -> i.getFirstName()).collect(Collectors.toList()) + '\'' : "") +
                '}';
    }
}