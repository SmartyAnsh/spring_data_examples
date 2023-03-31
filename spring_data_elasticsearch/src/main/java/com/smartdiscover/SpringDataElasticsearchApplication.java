package com.smartdiscover;

import com.smartdiscover.model.Book;
import com.smartdiscover.repositories.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootApplication
public class SpringDataElasticsearchApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataElasticsearchApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataElasticsearchApplication.class, args);
    }

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        Book book = new Book();
        book.setName("Zero to one");
        book.setSummary("Notes on startups, or how to build the future");
        bookRepository.save(book);

        Page<Book> page = bookRepository.findAll(Pageable.unpaged());

        log.info(String.valueOf(page.getContent()));
    }
}
