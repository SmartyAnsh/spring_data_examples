package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Document(indexName = "book")
public class Book {

    @Id
    private String id;

    private String name;

    private String summary;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Date dateCreated;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    private Date dateUpdated;

    private List<Author> authors;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ((null != authors) ? ", authors=" + authors.stream().map(i -> i.getFullName()).collect(Collectors.toList()) + '\'' : "") +
                ", createdBy='" + createdBy + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", dateUpdated='" + dateUpdated +
                '}';
    }
}
