package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@RedisHash("BookLoanEntry")
@Data
public class BookLoanEntry implements Serializable {

    private String id;

    private User user;

    private Book book;

    private Date loanDate;

    private Date dueDate;

    private Date returnDate;

    private String status;

}
