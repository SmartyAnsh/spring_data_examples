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
public class Book {

    @PrimaryKey
    private UUID id;

    @Column
    private String name;

    @Column
    private String summary;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Date dateCreated;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    private Date dateUpdated;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", dateUpdated='" + dateUpdated +
                '}';
    }
}