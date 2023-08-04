package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Data
@Table
public class Author {

    @PrimaryKey
    private UUID id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Date dateCreated;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    private Date dateUpdated;

    public String getFullName() {
        return lastName + " " + firstName;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", dateUpdated='" + dateUpdated +
                '}';
    }
}