package com.smartdiscover;

import com.smartdiscover.model.Book;
import com.smartdiscover.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import java.util.Iterator;

@SpringBootApplication
@EnableSolrRepositories(basePackages = "com.smartdiscover.repository")
public class SpringDataSolrApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataSolrApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataSolrApplication.class, args);
    }

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        Book book = new Book();
        book.setId("908765");
        book.setName("Zero to one");
        book.setSummary("Notes on startups, or how to build the future");

        bookRepository.save(book);

        Iterator iterator = bookRepository.findAll().iterator();
        while (iterator.hasNext()) {
            log.info(String.valueOf(iterator.next()));
        }
    }
}
