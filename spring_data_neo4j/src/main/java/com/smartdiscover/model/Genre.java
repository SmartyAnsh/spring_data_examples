package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Data
@Node("Genre")
public class Genre {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    private GenreText genreText;

    public Genre() {
    }

    public Genre(GenreText genreText) {
        this.genreText = genreText;
    }

    public enum GenreText {
        SCIENCE_FICTION,
        FICTION,
        THRILLER,
        ROMANCE,
        FANTASY,
        ADVENTURE,
        BIOGRAPHY,
        SELF_HELP,
        NONFICTION
    }

}


