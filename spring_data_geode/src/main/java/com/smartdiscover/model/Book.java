package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

@Region("Book")
@Data
public class Book {

    @Id
    private Long id;

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
