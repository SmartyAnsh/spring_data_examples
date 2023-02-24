package com.smartdiscover;

import com.smartdiscover.entity.Author;
import com.smartdiscover.entity.Book;
import com.smartdiscover.repository.AuthorRepository;
import com.smartdiscover.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootApplication
public class SpringDataJdbcApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataJdbcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJdbcApplication.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public void run(String... strings) throws Exception {

        log.info("searching tables");

        Book book = new Book();
        book.setName("Martian");
        book.setSummary("After a dust storm nearly kills him and forces his crew to evacuate while thinking him dead.");
        bookRepository.save(book);

        Author author = new Author();
        author.setFirstName("Andy");
        author.setLastName("Weir");
        authorRepository.save(author);

        jdbcTemplate.update("insert into author_book (author_id, book_id) values (?, ?)", author.getId(), book.getId());

        log.info(String.valueOf(bookRepository.findAll()));

        log.info(String.valueOf(authorRepository.findAll()));

        List list = jdbcTemplate.queryForList("select * from book order by id desc");
        ;
        log.info(String.valueOf(list));
    }

}
