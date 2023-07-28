package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Node("Book")
public class Book {

    @RelationshipId
    @GeneratedValue(UUIDStringGenerator.class)
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

    private List<Genre> genres;

    private List<BookRating> ratings;

    public double getAverageRating() {
        return ratings.stream().mapToInt(i -> i.getRating()).average().orElse(0.0);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ((null != authors && !authors.isEmpty()) ? ", authors=" + authors.stream().map(i -> i.getFullName()).collect(Collectors.toList()) + '\'' : "") +
                ((null != genres && !genres.isEmpty()) ? ", genres=" + genres.stream().map(i -> i.getGenreText()).collect(Collectors.toList()) + '\'' : "") +
                ((null != ratings && !ratings.isEmpty()) ? ", ratings=" + ratings.stream().map(i -> i.getRating()).collect(Collectors.toList()) + '\'' : "") +
                ", createdBy='" + createdBy + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", dateUpdated='" + dateUpdated +
                '}';
    }

}
