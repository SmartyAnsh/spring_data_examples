package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Book")
@Data
public class Book {

    private String id;
    private String name;
    private String summary;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }

}
