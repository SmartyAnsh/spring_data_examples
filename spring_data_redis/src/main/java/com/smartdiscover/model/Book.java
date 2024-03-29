package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@RedisHash("Book")
@Data
public class Book implements Serializable {

    private String id;

    @Indexed
    private String name;
    private String summary;

    private Boolean available;

    private List<Author> authors;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ((null != authors) ? ", authors=" + authors.stream().map(i -> i.getFullName()).collect(Collectors.toList()) + '\'' : "") +
                '}';
    }

}
