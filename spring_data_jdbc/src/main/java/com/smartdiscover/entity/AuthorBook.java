package com.smartdiscover.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "AUTHOR_BOOK")
public class AuthorBook {

    @Column("AUTHOR_ID")
    private long authorId;

    @Column("BOOK_ID")
    private long bookId;

    @Override
    public String toString() {
        return "AuthorBook{" +
                "authorId=" + authorId +
                ", bookId=" + bookId +
                '}';
    }
}
