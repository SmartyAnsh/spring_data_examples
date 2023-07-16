package com.smartdiscover.document;

import com.smartdiscover.document.Book;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class BookLoanEntry {

    @Id
    private String id;

    private String userFirstName;

    private String userLastName;

    @DBRef
    private Book book;

    private Date loanDate;

    private Date dueDate;

    private Date returnDate;

    private String status;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Date createdDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private Date lastModifiedDate;

}
