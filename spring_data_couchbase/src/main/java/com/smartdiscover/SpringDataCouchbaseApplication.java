package com.smartdiscover;

import com.smartdiscover.model.Book;
import com.smartdiscover.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDataCouchbaseApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataCouchbaseApplication.class, args);
    }

    private static final Logger log = LoggerFactory.getLogger(SpringDataCouchbaseApplication.class);

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        Book book = new Book();
        book.setName("Do Epic Shit");
        book.setSummary("Let's do it!");

        bookRepository.save(book);

        log.info(String.valueOf(bookRepository.findAll()));

    }
}
