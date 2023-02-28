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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class SpringDataJpaApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataJpaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaApplication.class, args);
    }

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Author author1 = new Author();
        author1.setFirstName("Peter");
        author1.setLastName("Thiel");
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setFirstName("Blake");
        author2.setLastName("Masters");
        authorRepository.save(author2);

        Book book = new Book();
        book.setName("Zero to one");
        book.setSummary("Notes on startups, or how to build the future");

        List<Author> authors = new ArrayList<>();
        authors.add(author1);
        authors.add(author2);

        book.setAuthors(authors);

        List<Book> books = new ArrayList<>();
        books.add(book);

        bookRepository.save(book);

        author1.setBooks(books);
        author2.setBooks(books);

        authorRepository.save(author1);
        authorRepository.save(author2);

        log.info(String.valueOf(bookRepository.findAll()));
        log.info(String.valueOf(authorRepository.findAll()));

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from book_authors");

        log.info(String.valueOf(rows));
    }
}
