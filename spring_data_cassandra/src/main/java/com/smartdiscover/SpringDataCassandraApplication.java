package com.smartdiscover;

import com.smartdiscover.model.Book;
import com.smartdiscover.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class SpringDataCassandraApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataCassandraApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataCassandraApplication.class, args);
    }

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        Book book = new Book();
        book.setId(UUID.randomUUID());
        book.setName("Zero to one");
        book.setSummary("Notes on startups, or how to build the future");

        bookRepository.save(book);

        log.info(String.valueOf(bookRepository.findAll()));
    }
}
